package com.trygod.prophiusassessment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostDto {

    @NotBlank(message = "Content cannot be blank")
    private String content;

    @NotNull(message = "User ID cannot be null")
    private Long userId;
}
