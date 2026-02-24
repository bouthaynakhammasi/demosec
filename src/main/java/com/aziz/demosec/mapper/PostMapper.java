package com.aziz.demosec.mapper;

import com.aziz.demosec.Entities.Post;
import com.aziz.demosec.dto.PostRequest;
import com.aziz.demosec.dto.PostResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostMapper {

    public Post toEntity(PostRequest dto) {
        return Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();
    }

    public PostResponse toDto(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .authorName(post.getAuthor().getFullName())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .build();
    }

    public List<Post> toEntities(List<PostRequest> dtos) {
        return dtos == null ? null : dtos.stream().map(this::toEntity).collect(Collectors.toList());
    }

    public List<PostResponse> toDtos(List<Post> entities) {
        return entities == null ? null : entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    public void updateFromDto(PostRequest dto, Post entity) {
        if (dto == null || entity == null) return;
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
    }
}
