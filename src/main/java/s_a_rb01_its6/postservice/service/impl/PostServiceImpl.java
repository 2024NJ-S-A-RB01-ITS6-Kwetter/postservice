package s_a_rb01_its6.postservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import s_a_rb01_its6.postservice.config.RabbitMQConfig;
import s_a_rb01_its6.postservice.dto.request.CreatePostRequest;
import s_a_rb01_its6.postservice.dto.response.CreatePostResponse;
import s_a_rb01_its6.postservice.repository.PostRepository;
import s_a_rb01_its6.postservice.service.PostService;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    PostRepository postRepository;


    @Override
    public CreatePostResponse createPost(CreatePostRequest createPostRequest) {
        // validate request like if the user exists, is not banned, etc

        // create post

        return CreatePostResponse.builder()
                .message("Post created successfully")
                .build();
    }


    @RabbitListener(queues = RabbitMQConfig.USER_DELETE_QUEUE)
    public void deleteUserPosts(Long userId) {
        System.out.println("User deleted, deleting all posts of user with id: " + userId);
        postRepository.deleteAllByAuthorId(userId);
    }



}
