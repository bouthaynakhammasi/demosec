package com.aziz.demosec.controller;

import com.aziz.demosec.dto.PostRequest;
import com.aziz.demosec.dto.PostResponse;
import com.aziz.demosec.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // ✅ Seulement équipe médicale peut créer
    @PostMapping
     public ResponseEntity<PostResponse> create(@Valid @RequestBody PostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.create(request));
    }

    // ✅ Tout le monde peut lire
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAll() {
        return ResponseEntity.ok(postService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getById(id));
    }

    // ✅ Seulement équipe médicale peut modifier
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'CLINIC', 'PHARMACIST', 'LABORATORY_STAFF', 'NUTRITIONIST', 'HOME_CARE_PROVIDER', 'ADMIN')")
    public ResponseEntity<PostResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.update(id, request));
    }

    // ✅ Seulement équipe médicale peut supprimer
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'CLINIC', 'PHARMACIST', 'LABORATORY_STAFF', 'NUTRITIONIST', 'HOME_CARE_PROVIDER', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }
}