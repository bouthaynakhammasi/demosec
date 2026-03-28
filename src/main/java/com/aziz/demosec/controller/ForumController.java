package com.aziz.demosec.controller;

import com.aziz.demosec.dto.CommentRequest;
import com.aziz.demosec.dto.CommentResponse;
import com.aziz.demosec.dto.PostRequest;
import com.aziz.demosec.dto.PostResponse;
import com.aziz.demosec.service.ForumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
@RequiredArgsConstructor
@Slf4j
public class ForumController {

    private final ForumService forumService;

    @GetMapping("/posts")
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        log.info("📋 RÉCUPÉRATION TOUS LES POSTS");
        List<PostResponse> posts = forumService.getAllPosts();
        log.info("✅ {} posts trouvés", posts.size());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        log.info("📋 RÉCUPÉRATION POST - ID: {}", id);
        PostResponse post = forumService.getPostById(id);
        log.info("✅ Post trouvé: {}", post.getTitle());
        return ResponseEntity.ok(post);
    }

    @PostMapping("/posts")
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest request) {
        log.info("📝 CRÉATION POST - Titre: {}", request.getTitle());
        PostResponse created = forumService.createPost(request);
        log.info("✅ Post créé - ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id, @Valid @RequestBody PostRequest request) {
        log.info("📝 MISE À JOUR POST - ID: {}, Titre: {}", id, request.getTitle());
        PostResponse updated = forumService.updatePost(id, request);
        log.info("✅ Post mis à jour - ID: {}", updated.getId());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        log.info("🗑️ SUPPRESSION POST - ID: {}", id);
        forumService.deletePost(id);
        log.info("✅ Post supprimé - ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long postId) {
        log.info("👍 LIKE POST - ID: {}", postId);
        forumService.likePost(postId);
        log.info("✅ Like ajouté - Post ID: {}", postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{postId}/like")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId) {
        log.info("👎 UNLIKE POST - ID: {}", postId);
        forumService.unlikePost(postId);
        log.info("✅ Like retiré - Post ID: {}", postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getPostComments(@PathVariable Long postId) {
        log.info("💬 RÉCUPÉRATION COMMENTAIRES - Post ID: {}", postId);
        List<CommentResponse> comments = forumService.getCommentsByPost(postId);
        log.info("✅ {} commentaires trouvés", comments.size());
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long postId, @Valid @RequestBody CommentRequest request) {
        log.info("💬 CRÉATION COMMENTAIRE - Post ID: {}", postId);
        CommentResponse created = forumService.createComment(postId, request);
        log.info("✅ Commentaire créé - ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long id, @Valid @RequestBody CommentRequest request) {
        log.info("💬 MISE À JOUR COMMENTAIRE - ID: {}", id);
        CommentResponse updated = forumService.updateComment(id, request);
        log.info("✅ Commentaire mis à jour - ID: {}", updated.getId());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        log.info("🗑️ SUPPRESSION COMMENTAIRE - ID: {}", id);
        forumService.deleteComment(id);
        log.info("✅ Commentaire supprimé - ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
