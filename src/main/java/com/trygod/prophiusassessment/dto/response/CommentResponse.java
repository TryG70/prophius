package com.trygod.prophiusassessment.dto.response;

import lombok.Data;

@Data
public class CommentResponse {

    private Long id;

    private String content;

    private Long postId;

    private Long userId;
}
