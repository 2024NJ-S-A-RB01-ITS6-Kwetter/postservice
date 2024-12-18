package s_a_rb01_its6.postservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import s_a_rb01_its6.postservice.dto.request.CreatePostRequest;
import s_a_rb01_its6.postservice.dto.request.DeletePostRequest;
import s_a_rb01_its6.postservice.dto.request.GetPostsRequest;
import s_a_rb01_its6.postservice.dto.request.SearchPostRequest;
import s_a_rb01_its6.postservice.dto.response.CreatePostResponse;
import s_a_rb01_its6.postservice.dto.response.DeletePostResponse;
import s_a_rb01_its6.postservice.dto.response.IndividualPost;
import s_a_rb01_its6.postservice.service.PostService;
import s_a_rb01_its6.postservice.service.exception.UnauthorizedDataAccessException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static s_a_rb01_its6.postservice.config.Constant.API_URL;

@RestController
@RequestMapping(value =API_URL + "/post")
@RequiredArgsConstructor
public class PostController {

    public static final String CURRENT_PAGE = "currentPage";
    public static final String TOTAL_PAGES = "totalPages";
    public static final String PAGE_SIZE = "pageSize";
    public static final String TOTAL_ELEMENTS = "totalElements";
    public static final String POSTS = "posts";

    private final PostService postService;

    //create post
    //TODO make sure that user is posting to own account
    @PostMapping("/create")
    public ResponseEntity<CreatePostResponse> createPost(@RequestBody CreatePostRequest createPostRequest) {
        // Get authentication details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Extract the 'sub' (user ID) from the JWT and use authentication.getName() for preferred_username
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userId = jwt.getClaimAsString("sub"); // User ID from JWT
        String preferredUsername = authentication.getName(); // Preferred username directly from authentication

        // Proceed with creating the post
        CreatePostResponse createPostResponse = postService.createPost(createPostRequest, userId, preferredUsername);
        return ResponseEntity.status(HttpStatus.CREATED).body(createPostResponse);
    }

    //get posts feed
    //TODO MAKE PAGED
    //TODO make this when friendservice is implemented
    @GetMapping("/feed")
    public ResponseEntity<String> getPostsFeed() {

        return ResponseEntity.ok("Posts feed");
    }

    //get posts on user profile
    //TODO limit visibility of posts to only friends
    @GetMapping("/profile/{username}")
    public ResponseEntity<Map<String, Object>> getPostsOnUserProfile(@PathVariable String username,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        System.out.println("username: " + username);
        Page<IndividualPost> posts = postService.getPostsOneUser(new GetPostsRequest(page, size, username));
        Map<String, Object> response = new HashMap<>();
        response.put(CURRENT_PAGE, posts.getNumber());
        response.put(TOTAL_PAGES, posts.getTotalPages());
        response.put(PAGE_SIZE, posts.getSize());
        response.put(TOTAL_ELEMENTS, posts.getTotalElements());
        response.put(POSTS, posts.getContent());

        return ResponseEntity.ok(response);
    }

    //get post single
    //TODO limit visibility of posts to only friends
    @GetMapping("/{postId}")
    public ResponseEntity<IndividualPost> getPostSingle(@PathVariable Long postId) {
        IndividualPost response =postService.getPost(postId);
        return ResponseEntity.ok(response);
    }


    //delete post
    @DeleteMapping("/delete")
    public ResponseEntity<DeletePostResponse> deletePost(@RequestBody DeletePostRequest deletePostRequest) {
        // Get authentication details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Extract the 'sub' (user ID) from the JWT and use authentication.getName() for preferred_username
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userId = jwt.getClaimAsString("sub");
        //check if user is the author of the post or admin
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (!userId.equals(deletePostRequest.getAuthorId()) && !isAdmin) {
            throw new UnauthorizedDataAccessException("You are not authorized to delete this post");
        }
        DeletePostResponse deletePostResponse = postService.deletePost(deletePostRequest.getPostId());

        return ResponseEntity.ok(deletePostResponse);
    }


    //search post
    //TODO limit visibility of posts to only friends
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchPost(
            @RequestParam String query,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        SearchPostRequest searchPostRequest = new SearchPostRequest(page, size, query);
        Page<IndividualPost> posts = postService.searchPosts(searchPostRequest);
        Map<String, Object> response = new HashMap<>();
        response.put(CURRENT_PAGE, posts.getNumber());
        response.put(TOTAL_PAGES, posts.getTotalPages());
        response.put(PAGE_SIZE, posts.getSize());
        response.put(TOTAL_ELEMENTS, posts.getTotalElements());
        response.put(POSTS, posts.getContent());

        return ResponseEntity.ok(response);


    }


}
