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
    private String authorRole;       // ✅ Rôle de l'auteur (ex: DOCTOR)
    private String title;
    private String content;
    private String category;         // ✅ Catégorie du post
    private LocalDateTime createdAt;
    private int commentsCount;       // ✅ Nombre de commentaires
    private int likesCount;          // ✅ Nombre de likes
}