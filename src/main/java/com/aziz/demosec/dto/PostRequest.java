package com.aziz.demosec.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {

    @NotNull
    private Long authorId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}