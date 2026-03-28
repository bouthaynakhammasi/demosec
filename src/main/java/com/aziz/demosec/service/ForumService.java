package com.aziz.demosec.service;

import com.aziz.demosec.dto.CommentRequest;
import com.aziz.demosec.dto.CommentResponse;
import com.aziz.demosec.dto.PostRequest;
import com.aziz.demosec.dto.PostResponse;

import java.util.List;

public interface ForumService {
    
    // Post operations
    List<PostResponse> getAllPosts();
    PostResponse getPostById(Long id);
    PostResponse createPost(PostRequest request);
    PostResponse updatePost(Long id, PostRequest request);
    void deletePost(Long id);
    
    // Like operations
    void likePost(Long postId);
    void unlikePost(Long postId);
    
    // Comment operations
    List<CommentResponse> getCommentsByPost(Long postId);
    CommentResponse createComment(Long postId, CommentRequest request);
    CommentResponse updateComment(Long id, CommentRequest request);
    void deleteComment(Long id);
}
