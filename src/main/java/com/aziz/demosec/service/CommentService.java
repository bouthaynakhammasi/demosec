package com.aziz.demosec.service;

import com.aziz.demosec.dto.CommentRequest;
import com.aziz.demosec.dto.CommentResponse;
import java.util.List;

public interface CommentService {
    CommentResponse create(CommentRequest request);
    CommentResponse getById(Long id);
    List<CommentResponse> getAll();
    List<CommentResponse> getByPostId(Long postId);
    CommentResponse update(Long id, CommentRequest request);
    void delete(Long id);
}