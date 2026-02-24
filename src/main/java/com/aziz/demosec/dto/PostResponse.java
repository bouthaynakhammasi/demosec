package com.aziz.demosec.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
    private Long id;
    private String authorName;
    private String title;
    private String content;
    private LocalDateTime createdAt;
}