package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // ✅ Trier par date décroissante (plus récent en premier)
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);

    // ✅ Compter les commentaires d'un post
    long countByPostId(Long postId);

    // ✅ Trouver les commentaires d'un auteur
    List<Comment> findByAuthorId(Long authorId);
}