package s_a_rb01_its6.postservice.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import s_a_rb01_its6.postservice.dto.request.CreatePostRequest;
import s_a_rb01_its6.postservice.dto.response.CreatePostResponse;


public interface PostService {
    //create post
    CreatePostResponse createPost(CreatePostRequest createPostRequest);
    //edit post

    //delete post

    //get post

    //get all post from a user paginated

    //get all posts from feed (friends) paginated (frontpage)


}
