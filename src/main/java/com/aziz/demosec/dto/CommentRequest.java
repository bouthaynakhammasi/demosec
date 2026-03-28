package com.aziz.demosec.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    @NotNull(message = "Post ID is required")
    private Long postId;

    @NotNull(message = "Author ID is required")
    private Long authorId;

    @NotBlank(message = "Content is required")
    private String content;
}