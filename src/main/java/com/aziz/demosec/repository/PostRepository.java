package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // ✅ Filtrer par catégorie
    List<Post> findByCategory(String category);

    // ✅ Filtrer par auteur
    List<Post> findByAuthorId(Long authorId);

    // ✅ Trier par date décroissante
    List<Post> findAllByOrderByCreatedAtDesc();

    @org.springframework.data.jpa.repository.Query("SELECT p.category, COUNT(p) as count FROM Post p GROUP BY p.category ORDER BY count DESC")
    List<Object[]> findTrendingCategories();
}