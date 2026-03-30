package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Like;

import com.aziz.demosec.Entities.Post;
import com.aziz.demosec.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    Optional<Like> findByPostAndUser(Post post, User user);
    
    @Query("SELECT COUNT(l) FROM Like l WHERE l.post = :post")
    int countByPost(@Param("post") Post post);
    boolean existsByPostAndUser(Post post, User user);
    
    void deleteByPostAndUser(Post post, User user);
}
