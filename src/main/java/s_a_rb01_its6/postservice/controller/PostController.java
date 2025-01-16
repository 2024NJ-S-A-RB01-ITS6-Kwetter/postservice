package s_a_rb01_its6.postservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;
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
import java.util.concurrent.CompletableFuture;

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

    // Create post (async)

    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<CreatePostResponse>> createPost(@RequestBody CreatePostRequest createPostRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // Capture authentication in the main thread
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userId = jwt.getClaimAsString("sub");
        String preferredUsername = authentication.getName();

        DelegatingSecurityContextExecutor securityExecutor = new DelegatingSecurityContextExecutor(Runnable::run);

        return CompletableFuture.supplyAsync(() -> {
            CreatePostResponse createPostResponse = postService.createPost(createPostRequest, userId, preferredUsername);
            return ResponseEntity.status(HttpStatus.CREATED).body(createPostResponse);
        }, securityExecutor);
    }

    // Get posts feed (async)
    @GetMapping("/feed")
    public CompletableFuture<ResponseEntity<String>> getPostsFeed() {
        return CompletableFuture.supplyAsync(() -> ResponseEntity.ok("Posts feed"));
    }

    // Get posts on user profile (async)
    @GetMapping("/profile/{username}")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> getPostsOnUserProfile(
            @PathVariable String username,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return CompletableFuture.supplyAsync(() -> {
            Page<IndividualPost> posts = postService.getPostsOneUser(new GetPostsRequest(page, size, username));
            Map<String, Object> response = new HashMap<>();
            response.put(CURRENT_PAGE, posts.getNumber());
            response.put(TOTAL_PAGES, posts.getTotalPages());
            response.put(PAGE_SIZE, posts.getSize());
            response.put(TOTAL_ELEMENTS, posts.getTotalElements());
            response.put(POSTS, posts.getContent());

            return ResponseEntity.ok(response);
        });
    }

    // Get single post (async)
    @GetMapping("/{postId}")
    public CompletableFuture<ResponseEntity<IndividualPost>> getPostSingle(@PathVariable Long postId) {
        return CompletableFuture.supplyAsync(() -> {
            IndividualPost response = postService.getPost(postId);
            return ResponseEntity.ok(response);
        });
    }

    // Delete post (async)
    @DeleteMapping("/delete")
    public CompletableFuture<ResponseEntity<DeletePostResponse>> deletePost(@RequestBody DeletePostRequest deletePostRequest) {
        return CompletableFuture.supplyAsync(() -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String userId = jwt.getClaimAsString("sub");
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

            if (!userId.equals(deletePostRequest.getAuthorId()) && !isAdmin) {
                throw new UnauthorizedDataAccessException("You are not authorized to delete this post");
            }

            DeletePostResponse deletePostResponse = postService.deletePost(deletePostRequest.getPostId());
            return ResponseEntity.ok(deletePostResponse);
        });
    }

    // Search posts (async)
    @GetMapping("/search")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> searchPost(
            @RequestParam String query,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return CompletableFuture.supplyAsync(() -> {
            SearchPostRequest searchPostRequest = new SearchPostRequest(page, size, query);
            Page<IndividualPost> posts = postService.searchPosts(searchPostRequest);
            Map<String, Object> response = new HashMap<>();
            response.put(CURRENT_PAGE, posts.getNumber());
            response.put(TOTAL_PAGES, posts.getTotalPages());
            response.put(PAGE_SIZE, posts.getSize());
            response.put(TOTAL_ELEMENTS, posts.getTotalElements());
            response.put(POSTS, posts.getContent());

            return ResponseEntity.ok(response);
        });
    }


}
