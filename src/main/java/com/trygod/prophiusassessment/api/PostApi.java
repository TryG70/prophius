package com.trygod.prophiusassessment.api;

import com.trygod.prophiusassessment.data.PostData;
import com.trygod.prophiusassessment.dto.PostDto;
import com.trygod.prophiusassessment.dto.response.MessageResponse;
import com.trygod.prophiusassessment.dto.response.PostResponse;
import com.trygod.prophiusassessment.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Post Controller", description = "Endpoints for managing posts")
public class PostApi {

    private final PostService<PostDto, PostData, PostResponse> postService;

    @PostMapping
    @Operation(summary = "Create a new post")
    public ResponseEntity<MessageResponse<PostResponse>> createPost(@RequestBody @Valid PostDto postDto) {
        PostResponse postResponse = postService.create(postDto);
        return new ResponseEntity<>(MessageResponse.response(postResponse), CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing post by ID")
    public ResponseEntity<MessageResponse<PostResponse>> updatePost(
            @PathVariable @Parameter(description = "Post ID", required = true) Long id,
            @RequestBody PostDto postDto
    ) {
        PostResponse postResponse = postService.update(id, postDto);
        return ResponseEntity.ok(MessageResponse.response(postResponse));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a post by ID")
    public ResponseEntity<Void> deletePost(@PathVariable @Parameter(description = "Post ID", required = true) Long id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a post by ID")
    public ResponseEntity<MessageResponse<PostResponse>> getPostById(
            @PathVariable @Parameter(description = "Post ID", required = true) Long id
    ) {
        PostData postData = postService.findById(id);
        PostResponse postResponse = new PostResponse();
        BeanUtils.copyProperties(postData, postResponse);
        return ResponseEntity.ok(MessageResponse.response(postResponse));
    }

    @GetMapping(params = {"page", "size", "sort", "search"})
    @Operation(summary = "Search for posts by keyword")
    public ResponseEntity<MessageResponse<Page<PostResponse>>> searchPosts(
            @RequestParam @Parameter(description = "Keyword to search for", required = true) String keyWord,
            Pageable pageable
    ) {
        MessageResponse<Page<PostResponse>> postResponsePage = postService.search(keyWord, pageable);
        return ResponseEntity.ok(postResponsePage);
    }

    @PatchMapping("/{postId}/like/{userId}")
    @Operation(summary = "Like a post")
    public ResponseEntity<Void> likePost(
            @PathVariable @Parameter(description = "Post ID", required = true) Long postId,
            @PathVariable @Parameter(description = "User ID", required = true) Long userId
    ) {
        postService.likePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{postId}/unlike/{userId}")
    @Operation(summary = "Unlike a post")
    public ResponseEntity<Void> unlikePost(
            @PathVariable @Parameter(description = "Post ID", required = true) Long postId,
            @PathVariable @Parameter(description = "User ID", required = true) Long userId
    ) {
        postService.unlikePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/user/{userId}/page", params = {"page", "size", "sort"})
    @Operation(summary = "Get all posts with pagination")
    public ResponseEntity<MessageResponse<Page<PostResponse>>> getAllPosts(@PathVariable @Parameter(description = "User ID", required = true) Long userId,
                                                                            Pageable pageable) {
        MessageResponse<Page<PostResponse>> postResponsePage = postService.findAll(userId, pageable);
        return ResponseEntity.ok(postResponsePage);
    }
}
