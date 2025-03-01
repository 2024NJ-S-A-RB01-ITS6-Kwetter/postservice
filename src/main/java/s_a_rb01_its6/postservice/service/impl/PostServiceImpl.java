package s_a_rb01_its6.postservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import s_a_rb01_its6.postservice.config.RabbitMQConfig;
import s_a_rb01_its6.postservice.dto.request.BadWordsRequest;
import s_a_rb01_its6.postservice.dto.request.CreatePostRequest;
import s_a_rb01_its6.postservice.dto.request.GetPostsRequest;
import s_a_rb01_its6.postservice.dto.request.SearchPostRequest;
import s_a_rb01_its6.postservice.dto.response.BadWordResponse;
import s_a_rb01_its6.postservice.dto.response.CreatePostResponse;
import s_a_rb01_its6.postservice.dto.response.DeletePostResponse;
import s_a_rb01_its6.postservice.dto.response.IndividualPost;
import s_a_rb01_its6.postservice.event.UserUpdatedEvent;
import s_a_rb01_its6.postservice.repository.PostRepository;
import s_a_rb01_its6.postservice.repository.entities.PostEntity;
import s_a_rb01_its6.postservice.service.PostService;
import s_a_rb01_its6.postservice.service.exception.BadWordsException;
import s_a_rb01_its6.postservice.service.exception.OutOfBoundPageException;
import s_a_rb01_its6.postservice.service.impl.DTOConverter.PostDTOConverter;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final RestTemplate restTemplate;

    @Value("${azure.function.url}")
    private String badWordUrl;

    @Override
    public CreatePostResponse createPost(CreatePostRequest createPostRequest, String userId, String username) {
        // Check if the content contains user mentions or bad words
        if (!isContentAllowed(createPostRequest.getContent())) {
            throw new BadWordsException("Post contains bad words");
        }

        // Create the post entity
        Instant now = Instant.now();
        PostEntity postEntity = PostEntity.builder()
                .authorId(userId)
                .authorUsername(username)
                .content(createPostRequest.getContent())
                .createdAt(Date.from(now))
                .build();

        // Save the post entity and get the saved entity (including generated ID)
        PostEntity savedPostEntity = postRepository.save(postEntity);

        // Return the response with the ID of the created post
        return CreatePostResponse.builder()
                .id(savedPostEntity.getId()) // Include the generated ID
                .message("Post created successfully")
                .build();
    }



    private boolean isContentAllowed(String content) {
        try {
            // Send content to Azure Function
            BadWordsRequest request = new BadWordsRequest(content);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    badWordUrl,
                    request,
                    String.class // Handle the response as a JSON string
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Parse the JSON response
                ObjectMapper objectMapper = new ObjectMapper();
                BadWordResponse badWordResponse = objectMapper.readValue(response.getBody(), BadWordResponse.class);

                // If `containsBadWords` is true, the content is denied
                return !Boolean.TRUE.equals(badWordResponse.getDenied());
            } else {
                System.out.println("Bad word check failed: " + response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error while checking bad words: " + e.getMessage());
            return false;
        }
    }




    //get post by id
    @Override
    public IndividualPost getPost(Long postId) {
        PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found"));
        return PostDTOConverter.convertToIndividualPost(postEntity);
    }

    //get posts feed


    //get posts on user profile
    @Override
    public Page<IndividualPost> getPostsOneUser(GetPostsRequest getPostsRequest) {
        // Out of bounds check
        if (getPostsRequest.getPage() <= 0) {
            throw new OutOfBoundPageException("Page number cannot be negative or zero");
        }

        // Create pageable with sort by date descending
        Pageable pageable = PageRequest.of(
                getPostsRequest.getPage() - 1,
                getPostsRequest.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt") // Replace "createdDate" with your actual date field
        );

        // Get the author ID associated with the username
        Optional<String> authorID = postRepository.findDistinctAuthorIdByAuthorUsername(getPostsRequest.getUsername());
        if (authorID.isEmpty()) {
            // If the user is not found, return an empty page
            return Page.empty(pageable);
        }

        // Retrieve posts for the author ID
        Page<PostEntity> posts = postRepository.getAllByAuthorId(authorID.get(), pageable);

        // Out of bounds check
        if (getPostsRequest.getPage() > posts.getTotalPages() && getPostsRequest.getPage() != 1) {
            throw new OutOfBoundPageException("Page number is out of bounds");
        }

        // Convert to the required DTO format
        return posts.map(PostDTOConverter::convertToIndividualPost);
    }


    //delete post
    @Override
    public DeletePostResponse deletePost(Long postId) {
        //check if post exists
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Post not found");
        }
        postRepository.deleteById(postId);
        return DeletePostResponse.builder()
                .message("Post deleted successfully")
                .build();
    }
    //search post
    @Override
    public Page<IndividualPost> searchPosts(SearchPostRequest searchPostRequest) {
        // out of bound check
        if (searchPostRequest.getPage() <= 0) {
            throw new OutOfBoundPageException("Page number cannot be negative or zero");
        }

        // minus 1 because page starts indexing at 0
        Pageable pageable = PageRequest.of(searchPostRequest.getPage() - 1, searchPostRequest.getSize());

        Page<PostEntity> posts = postRepository.findByContentContainingIgnoreCase(
                searchPostRequest.getQuery(),
                pageable
        );
        // out of bound check
        if (searchPostRequest.getPage() > posts.getTotalPages() && searchPostRequest.getPage() != 1) {
            throw new OutOfBoundPageException("Page number is out of bounds");
        }

        return posts.map(PostDTOConverter::convertToIndividualPost);
    }


    @Transactional
    @RabbitListener(queues = RabbitMQConfig.USER_DELETE_QUEUE)
    public void deleteUserPosts(String userId) {
        System.out.println("User deleted, deleting all posts of user with id: " + userId);
        postRepository.deleteAllByAuthorId(userId);
    }
    @Transactional
    @RabbitListener(queues = RabbitMQConfig.USER_UPDATE_QUEUE)
    public void updateUserPosts(UserUpdatedEvent userUpdatedEvent) {
        System.out.println("User updated, updating all posts of user with id: " + userUpdatedEvent.getId());
        postRepository.updateAllUsernamesByAuthorId(userUpdatedEvent.getUsername(), userUpdatedEvent.getId());
    }


}
