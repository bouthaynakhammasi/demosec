package com.aziz.demosec.controller;

import com.aziz.demosec.dto.CommentRequest;
import com.aziz.demosec.dto.CommentResponse;
import com.aziz.demosec.dto.PostRequest;
import com.aziz.demosec.dto.PostResponse;
import com.aziz.demosec.dto.PostUploadRequest;
import com.aziz.demosec.service.ForumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/forum")
@RequiredArgsConstructor
@Slf4j
public class ForumController {

    private final ForumService forumService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("🧪 TEST FORUM CONTROLLER");
        return ResponseEntity.ok("ForumController is working!");
    }

    @PostMapping("/test-upload")
    public ResponseEntity<String> testUpload(@RequestParam("title") String title) {
        log.info("🧪 TEST UPLOAD - Title: {}", title);
        return ResponseEntity.ok("Upload test received: " + title);
    }

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

    @PostMapping("/test-create")
    public ResponseEntity<String> testCreate(@RequestBody Map<String, Object> data) {
        log.info("🧪 TEST CREATE - Data: {}", data);
        return ResponseEntity.ok("Create test received: " + data.toString());
    }

    @PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('DOCTOR', 'CLINIC', 'PHARMACIST', 'LABORATORY_STAFF', 'NUTRITIONIST', 'HOME_CARE_PROVIDER', 'ADMIN')")
    public ResponseEntity<PostResponse> createPost(
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart("category") String category,
            @RequestPart("authorId") String authorId,  // ← String au lieu de Long
            @RequestPart(value = "image", required = false) MultipartFile image) {

        log.info("📝 CRÉATION POST - Titre: {}, Auteur: {}, Image: {}", title, authorId, image != null ? image.getOriginalFilename() : "none");

        try {
            // Validation manuelle
            if (title == null || title.trim().isEmpty()) {
                log.error("❌ Titre manquant");
                return ResponseEntity.badRequest().build();
            }
            if (content == null || content.trim().isEmpty()) {
                log.error("❌ Contenu manquant");
                return ResponseEntity.badRequest().build();
            }
            if (category == null || category.trim().isEmpty()) {
                log.error("❌ Catégorie manquante");
                return ResponseEntity.badRequest().build();
            }
            if (authorId == null) {
                log.error("❌ Author ID manquant");
                return ResponseEntity.badRequest().build();
            }

            PostUploadRequest uploadRequest = new PostUploadRequest();
            uploadRequest.setTitle(title.trim());
            uploadRequest.setContent(content.trim());
            uploadRequest.setCategory(category.trim());
            uploadRequest.setAuthorId(Long.parseLong(authorId)); // ← parse ici

            PostResponse created = forumService.createPostWithImage(uploadRequest, image);
            log.info("✅ Post créé - ID: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            log.error("❌ Erreur création post: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/posts/json")
    @PreAuthorize("hasAnyRole('DOCTOR', 'CLINIC', 'PHARMACIST', 'LABORATORY_STAFF', 'NUTRITIONIST', 'HOME_CARE_PROVIDER', 'ADMIN')")
    public ResponseEntity<PostResponse> createPostJson(@Valid @RequestBody PostRequest request) {
        log.info("📝 CRÉATION POST JSON - Titre: {}", request.getTitle());
        PostResponse created = forumService.createPost(request, null);
        log.info("✅ Post créé - ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/posts/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'CLINIC', 'PHARMACIST', 'LABORATORY_STAFF', 'NUTRITIONIST', 'HOME_CARE_PROVIDER', 'ADMIN')")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id, @Valid @RequestBody PostRequest request) {
        log.info("📝 MISE À JOUR POST - ID: {}, Titre: {}", id, request.getTitle());
        PostResponse updated = forumService.updatePost(id, request);
        log.info("✅ Post mis à jour - ID: {}", updated.getId());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/posts/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'CLINIC', 'PHARMACIST', 'LABORATORY_STAFF', 'NUTRITIONIST', 'HOME_CARE_PROVIDER', 'ADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        log.info("🗑️ SUPPRESSION POST - ID: {}", id);
        forumService.deletePost(id);
        log.info("✅ Post supprimé - ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/posts/{postId}/like")
    @PreAuthorize("hasAnyRole('DOCTOR', 'CLINIC', 'PHARMACIST', 'LABORATORY_STAFF', 'NUTRITIONIST', 'HOME_CARE_PROVIDER', 'ADMIN', 'PATIENT')")
    public ResponseEntity<Void> likePost(@PathVariable Long postId) {
        log.info("👍 LIKE POST - ID: {}", postId);
        forumService.likePost(postId);
        log.info("✅ Like ajouté - Post ID: {}", postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{postId}/like")
    @PreAuthorize("hasAnyRole('DOCTOR', 'CLINIC', 'PHARMACIST', 'LABORATORY_STAFF', 'NUTRITIONIST', 'HOME_CARE_PROVIDER', 'ADMIN', 'PATIENT')")
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
    @PreAuthorize("hasAnyRole('DOCTOR', 'CLINIC', 'PHARMACIST', 'LABORATORY_STAFF', 'NUTRITIONIST', 'HOME_CARE_PROVIDER', 'ADMIN', 'PATIENT')")
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long postId, @Valid @RequestBody CommentRequest request) {
        log.info("💬 CRÉATION COMMENTAIRE - Post ID: {}", postId);
        CommentResponse created = forumService.createComment(postId, request);
        log.info("✅ Commentaire créé - ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/comments/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'CLINIC', 'PHARMACIST', 'LABORATORY_STAFF', 'NUTRITIONIST', 'HOME_CARE_PROVIDER', 'ADMIN', 'PATIENT')")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long id, @Valid @RequestBody CommentRequest request) {
        log.info("💬 MISE À JOUR COMMENTAIRE - ID: {}", id);
        CommentResponse updated = forumService.updateComment(id, request);
        log.info("✅ Commentaire mis à jour - ID: {}", updated.getId());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/comments/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'CLINIC', 'PHARMACIST', 'LABORATORY_STAFF', 'NUTRITIONIST', 'HOME_CARE_PROVIDER', 'ADMIN')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        log.info("🗑️ SUPPRESSION COMMENTAIRE - ID: {}", id);
        forumService.deleteComment(id);
        log.info("✅ Commentaire supprimé - ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/trending-categories")
    public ResponseEntity<java.util.List<java.util.Map<String, Object>>> getTrendingCategories() {
        log.info("🔥 RÉCUPÉRATION CATÉGORIES POPULAIRES");
        return ResponseEntity.ok(forumService.getTrendingCategories());
    }
}
