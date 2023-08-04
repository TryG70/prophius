package com.trygod.prophiusassessment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentDto {

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Post Id is required")
    private Long postId;

    @NotNull(message = "User Id is required")
    private Long userId;
}
