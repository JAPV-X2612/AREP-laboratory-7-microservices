package edu.eci.arep.controller;

import edu.eci.arep.dto.PostRequest;
import edu.eci.arep.dto.PostResponse;
import edu.eci.arep.entity.Post;
import edu.eci.arep.service.PostService;
import edu.eci.arep.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller that handles post creation (protected) and listing (public).
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
@RestController
@RequestMapping("/api/posts")
@Tag(name = "Posts", description = "Post creation and retrieval")
public class PostController {

    private final PostService postService;
    private final UserService userService;

    /**
     * @param postService service for post persistence
     * @param userService service for user auto-provisioning on post creation
     */
    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    /**
     * Returns all posts ordered from newest to oldest (public endpoint).
     *
     * @return 200 with list of PostResponse DTOs
     */
    @GetMapping
    @Operation(summary = "List all posts", description = "Returns all posts ordered newest first. No authentication required.")
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        List<PostResponse> posts = postService.getAllPosts().stream()
                .map(PostResponse::from)
                .toList();
        return ResponseEntity.ok(posts);
    }

    /**
     * Creates a new post authored by the currently authenticated user.
     * Auto-provisions the user record if this is their first interaction.
     *
     * @param request the post content (validated, max 140 chars)
     * @param jwt     the validated Auth0 JWT
     * @return 201 with the created PostResponse
     */
    @PostMapping
    @Operation(summary = "Create a post", description = "Creates a new post. Requires a valid Auth0 JWT.")
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest request,
                                                   @AuthenticationPrincipal Jwt jwt) {
        String auth0Id = jwt.getSubject();
        userService.resolveUser(auth0Id,
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("nickname"));
        Post post = postService.createPost(request.getContent(), auth0Id);
        return ResponseEntity.status(HttpStatus.CREATED).body(PostResponse.from(post));
    }
}
