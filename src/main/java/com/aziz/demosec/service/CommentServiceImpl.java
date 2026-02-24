package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Comment;
import com.aziz.demosec.Entities.Post;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.CommentRequest;
import com.aziz.demosec.dto.CommentResponse;
import com.aziz.demosec.exception.ResourceNotFoundException;
import com.aziz.demosec.mapper.CommentMapper;
import com.aziz.demosec.repository.CommentRepository;
import com.aziz.demosec.repository.PostRepository;
import com.aziz.demosec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentResponse create(CommentRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Post not found with id: " + request.getPostId()));

        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + request.getAuthorId()));

        Comment comment = commentMapper.toEntity(request);
        comment.setPost(post);
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public CommentResponse getById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comment not found with id: " + id));
        return commentMapper.toDto(comment);
    }

    @Override
    public List<CommentResponse> getAll() {
        return commentRepository.findAll()
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> getByPostId(Long postId) {
        return commentRepository.findByPostId(postId)
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponse update(Long id, CommentRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comment not found with id: " + id));

        commentMapper.updateFromDto(request, comment);

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public void delete(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }
}