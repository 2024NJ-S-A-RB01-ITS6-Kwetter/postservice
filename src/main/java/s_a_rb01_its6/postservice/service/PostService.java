package s_a_rb01_its6.postservice.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import s_a_rb01_its6.postservice.dto.request.CreatePostRequest;
import s_a_rb01_its6.postservice.dto.request.GetPostsRequest;
import s_a_rb01_its6.postservice.dto.response.CreatePostResponse;
import s_a_rb01_its6.postservice.dto.response.DeletePostResponse;
import s_a_rb01_its6.postservice.dto.response.IndividualPost;


public interface PostService {
    //create post
    CreatePostResponse createPost(CreatePostRequest createPostRequest, String userId, String username);


    //edit post


    //get post
    IndividualPost getPost(Long postId);

    //get all post from a user paginated
    Page<IndividualPost> getPostsOneUser(GetPostsRequest getPostsRequest);

    //delete post
    DeletePostResponse deletePost(Long postId);

    //get all posts from feed (friends) paginated (frontpage)


}
