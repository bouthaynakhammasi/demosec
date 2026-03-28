package com.aziz.demosec.service;

import com.aziz.demosec.dto.PostRequest;
import com.aziz.demosec.dto.PostResponse;
import java.util.List;

public interface PostService {
    PostResponse create(PostRequest request);
    PostResponse getById(Long id);
    List<PostResponse> getAll();
    List<PostResponse> getByCategory(String category);  // ✅ ajouté
    PostResponse update(Long id, PostRequest request);
    void delete(Long id);
}