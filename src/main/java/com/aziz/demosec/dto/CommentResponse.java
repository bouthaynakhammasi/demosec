package com.aziz.demosec.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Long id;
    private Long postId;
    private String authorName;
    private String authorRole;
    private String content;
    private LocalDateTime createdAt;
}