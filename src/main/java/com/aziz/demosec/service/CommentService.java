package com.aziz.demosec.service;

import com.aziz.demosec.dto.CommentRequest;
import com.aziz.demosec.dto.CommentResponse;

import java.util.List;

public interface CommentService {

    // ✅ Créer un commentaire
    CommentResponse create(CommentRequest request);

    // ✅ Récupérer un commentaire par ID
    CommentResponse getById(Long id);

    // ✅ Récupérer tous les commentaires
    List<CommentResponse> getAll();

    // ✅ Récupérer commentaires d'un post (triés par date)
    List<CommentResponse> getByPostId(Long postId);

    // ✅ Modifier un commentaire
    CommentResponse update(Long id, CommentRequest request);

    // ✅ Supprimer un commentaire
    void delete(Long id);
}