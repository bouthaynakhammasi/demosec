package com.aziz.demosec.service;

import com.aziz.demosec.Entities.Like;
import com.aziz.demosec.Entities.Notification;
import com.aziz.demosec.Entities.NotificationType;
import com.aziz.demosec.Entities.Post;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.CommentRequest;
import com.aziz.demosec.dto.CommentResponse;
import com.aziz.demosec.dto.PostRequest;
import com.aziz.demosec.dto.PostResponse;
import com.aziz.demosec.dto.PostUploadRequest;
import com.aziz.demosec.exception.ResourceNotFoundException;
import com.aziz.demosec.repository.LikeRepository;
import com.aziz.demosec.repository.NotificationRepository;
import com.aziz.demosec.repository.PostRepository;
import com.aziz.demosec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
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
    private final NotificationRepository notificationRepository;

    @Override
    public List<PostResponse> getAllPosts() {
        log.info(" Récupération de tous les posts");
        return postService.getAll();
    }

    @Override
    public PostResponse getPostById(Long id) {
        log.info(" Récupération du post - ID: {}", id);
        return postService.getById(id);
    }

    @Override
    public PostResponse createPost(PostRequest request, MultipartFile image) {
        log.info("📝 Création d'un post - Titre: {}, Image: {}", request.getTitle(), image != null ? image.getOriginalFilename() : "none");
        return postService.create(request, image);
    }

    @Override
    public PostResponse createPostWithImage(PostUploadRequest request, MultipartFile image) {
        log.info(" Création d'un post avec image - Titre: {}, Image: {}", request.getTitle(), image != null ? image.getOriginalFilename() : "none");
        
        PostRequest postRequest = new PostRequest();
        postRequest.setTitle(request.getTitle());
        postRequest.setContent(request.getContent());
        postRequest.setCategory(request.getCategory());
        postRequest.setAuthorId(request.getAuthorId());
        
        return postService.create(postRequest, image);
    }

    @Override
    public PostResponse updatePost(Long id, PostRequest request) {
        log.info(" Mise à jour du post - ID: {}, Titre: {}", id, request.getTitle());
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

        // 🔔 Notification
        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            Notification notif = Notification.builder()
                .recipient(post.getAuthor())
                .title("New Like")
                .message(currentUser.getFullName() + " liked your post: " + post.getTitle())
                .type(NotificationType.LIKE)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .relatedId(post.getId())
                .build();
            notificationRepository.save(notif);
        }
    }

    @Override
    @Transactional
    public void unlikePost(Long postId) {
        log.info("👎 Unlike du post - ID: {}", postId);
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        
        User currentUser = getCurrentUser();
        
        if (!likeRepository.existsByPostAndUser(post, currentUser)) {
            return;
        }
        
        likeRepository.deleteByPostAndUser(post, currentUser);
    }

    @Override
    public List<CommentResponse> getCommentsByPost(Long postId) {
        return commentService.getByPostId(postId);
    }

    @Override
    @Transactional
    public CommentResponse createComment(Long postId, CommentRequest request) {
        log.info("💬 Création d'un commentaire - Post ID: {}", postId);
        request.setPostId(postId);
        CommentResponse commentResponse = commentService.create(request);
        
        // 🔔 Notification
        Post post = postRepository.findById(postId).orElse(null);
        User currentUser = getCurrentUser();
        if (post != null && !post.getAuthor().getId().equals(currentUser.getId())) {
             Notification notif = Notification.builder()
                .recipient(post.getAuthor())
                .title("New Comment")
                .message(currentUser.getFullName() + " commented on your post: " + post.getTitle())
                .type(NotificationType.COMMENT)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .relatedId(post.getId())
                .build();
            notificationRepository.save(notif);
        }

        return commentResponse;
    }

    @Override
    public CommentResponse updateComment(Long id, CommentRequest request) {
        return commentService.update(id, request);
    }

    @Override
    public void deleteComment(Long id) {
        commentService.delete(id);
    }

    @Override
    public java.util.List<java.util.Map<String, Object>> getTrendingCategories() {
        log.info("🔥 Récupération des catégories populaires");
        java.util.List<Object[]> results = postRepository.findTrendingCategories();
        return results.stream()
                .map(result -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("category", result[0]);
                    map.put("count", result[1]);
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
