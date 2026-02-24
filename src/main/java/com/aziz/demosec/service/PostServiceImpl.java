package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Post;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.PostRequest;
import com.aziz.demosec.dto.PostResponse;
import com.aziz.demosec.exception.ResourceNotFoundException;
import com.aziz.demosec.mapper.PostMapper;
import com.aziz.demosec.repository.PostRepository;
import com.aziz.demosec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    @Override
    public PostResponse create(PostRequest request) {
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + request.getAuthorId()));

        Post post = postMapper.toEntity(request);
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());

        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    public PostResponse getById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Post not found with id: " + id));
        return postMapper.toDto(post);
    }

    @Override
    public List<PostResponse> getAll() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PostResponse update(Long id, PostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Post not found with id: " + id));

        postMapper.updateFromDto(request, post);

        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    public void delete(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post not found with id: " + id);
        }
        postRepository.deleteById(id);
    }
}