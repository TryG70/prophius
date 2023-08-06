package com.trygod.prophiusassessment.api;

import com.trygod.prophiusassessment.data.CommentData;
import com.trygod.prophiusassessment.dto.CommentDto;
import com.trygod.prophiusassessment.dto.response.CommentResponse;
import com.trygod.prophiusassessment.dto.response.MessageResponse;
import com.trygod.prophiusassessment.service.CommentService;
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
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comment Controller", description = "Endpoints for managing comments")
public class CommentApi {

    private final CommentService<CommentDto, CommentData, CommentResponse> commentService;

    @PostMapping
    @Operation(summary = "Create a new comment")
    public ResponseEntity<MessageResponse<CommentResponse>> createComment(@RequestBody @Valid CommentDto commentDto) {
        CommentResponse commentResponse = commentService.create(commentDto);
        return new ResponseEntity<>(MessageResponse.response(commentResponse), CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing comment by ID")
    public ResponseEntity<MessageResponse<CommentResponse>> updateComment(
            @PathVariable @Parameter(description = "Comment ID", required = true) Long id,
            @RequestBody CommentDto commentDto
    ) {
        CommentResponse commentResponse = commentService.update(id, commentDto);
        return ResponseEntity.ok(MessageResponse.response(commentResponse));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a comment by ID")
    public ResponseEntity<Void> deleteComment(@PathVariable @Parameter(description = "Comment ID", required = true) Long id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a comment by ID")
    public ResponseEntity<MessageResponse<CommentResponse>> getCommentById(
            @PathVariable @Parameter(description = "Comment ID", required = true) Long id
    ) {
        CommentData commentData = commentService.findById(id);
        CommentResponse commentResponse = new CommentResponse();
        BeanUtils.copyProperties(commentData, commentResponse);
        return ResponseEntity.ok(MessageResponse.response(commentResponse));
    }

    @GetMapping(value = "/post/{postId}/page", params = {"page", "size", "sort"})
    @Operation(summary = "Get all comments with pagination")
    public ResponseEntity<MessageResponse<Page<CommentResponse>>> getAllComments(@PathVariable @Parameter(description = "Post ID", required = true) Long postId,
                                                                            Pageable pageable) {
        MessageResponse<Page<CommentResponse>> commentResponsePage = commentService.findAll(postId, pageable);
        return ResponseEntity.ok(commentResponsePage);
    }
}
