package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Like;
import com.aziz.demosec.Entities.Post;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.CommentRequest;
import com.aziz.demosec.dto.CommentResponse;
import com.aziz.demosec.dto.PostRequest;
import com.aziz.demosec.dto.PostResponse;
import com.aziz.demosec.exception.ResourceNotFoundException;
import com.aziz.demosec.repository.LikeRepository;
import com.aziz.demosec.repository.PostRepository;
import com.aziz.demosec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForumServiceImpl implements ForumService {

    private final PostService postService;
    private final CommentService commentService;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public List<PostResponse> getAllPosts() {
        log.info("📋 Récupération de tous les posts");
        return postService.getAll();
    }

    @Override
    public PostResponse getPostById(Long id) {
        log.info("📋 Récupération du post - ID: {}", id);
        return postService.getById(id);
    }

    @Override
    public PostResponse createPost(PostRequest request) {
        log.info("📝 Création d'un post - Titre: {}", request.getTitle());
        return postService.create(request);
    }

    @Override
    public PostResponse updatePost(Long id, PostRequest request) {
        log.info("📝 Mise à jour du post - ID: {}, Titre: {}", id, request.getTitle());
        return postService.update(id, request);
    }

    @Override
    public void deletePost(Long id) {
        log.info("🗑️ Suppression du post - ID: {}", id);
        postService.delete(id);
    }

    @Override
    @Transactional
    public void likePost(Long postId) {
        log.info("👍 Like du post - ID: {}", postId);
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        
        User currentUser = getCurrentUser();
        
        if (likeRepository.existsByPostAndUser(post, currentUser)) {
            log.warn("⚠️ L'utilisateur a déjà aimé ce post - Post ID: {}, User ID: {}", postId, currentUser.getId());
            return;
        }
        
        Like like = Like.builder()
                .post(post)
                .user(currentUser)
                .build();
        
        likeRepository.save(like);
        log.info("✅ Like ajouté - Post ID: {}, User ID: {}", postId, currentUser.getId());
    }

    @Override
    @Transactional
    public void unlikePost(Long postId) {
        log.info("👎 Unlike du post - ID: {}", postId);
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        
        User currentUser = getCurrentUser();
        
        if (!likeRepository.existsByPostAndUser(post, currentUser)) {
            log.warn("⚠️ L'utilisateur n'a pas encore aimé ce post - Post ID: {}, User ID: {}", postId, currentUser.getId());
            return;
        }
        
        likeRepository.deleteByPostAndUser(post, currentUser);
        log.info("✅ Like retiré - Post ID: {}, User ID: {}", postId, currentUser.getId());
    }

    @Override
    public List<CommentResponse> getCommentsByPost(Long postId) {
        log.info("💬 Récupération des commentaires - Post ID: {}", postId);
        return commentService.getByPostId(postId);
    }

    @Override
    public CommentResponse createComment(Long postId, CommentRequest request) {
        log.info("💬 Création d'un commentaire - Post ID: {}", postId);
        
        // Set the post ID in the request
        request.setPostId(postId);
        
        return commentService.create(request);
    }

    @Override
    public CommentResponse updateComment(Long id, CommentRequest request) {
        log.info("💬 Mise à jour du commentaire - ID: {}", id);
        return commentService.update(id, request);
    }

    @Override
    public void deleteComment(Long id) {
        log.info("🗑️ Suppression du commentaire - ID: {}", id);
        commentService.delete(id);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
