package com.prothsync.prothsync.controller;

import com.prothsync.prothsync.dto.PostCreateRequestDTO;
import com.prothsync.prothsync.dto.PostResponseDTO;
import com.prothsync.prothsync.dto.PostSummaryResponseDTO;
import com.prothsync.prothsync.dto.PostUpdateRequestDTO;
import com.prothsync.prothsync.entity.post.PostCategory;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.security.CustomUserDetails;
import com.prothsync.prothsync.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody PostCreateRequestDTO request
    ){
        PostResponseDTO response = postService.createPost(userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> getPost(
        @PathVariable Long postId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PostResponseDTO response = postService.getPost(postId, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<PostSummaryResponseDTO>> getPublicPosts(Pageable pageable) {
        PageResponse<PostSummaryResponseDTO> response = postService.getPublicPosts(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<PageResponse<PostSummaryResponseDTO>> getUserPosts(
        @PathVariable Long userId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        Pageable pageable
    ) {
        PageResponse<PostSummaryResponseDTO> response = postService.getUserPosts(
            userId, userDetails.getUserId(), pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<PageResponse<PostSummaryResponseDTO>> getPostsByCategory(
        @PathVariable PostCategory category,
        Pageable pageable
    ) {
        PageResponse<PostSummaryResponseDTO> response = postService.getPostsByCategory(
            category, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> updatePost(
        @PathVariable Long postId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody PostUpdateRequestDTO request
    ) {
        PostResponseDTO response = postService.updatePost(
            postId, userDetails.getUserId(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
        @PathVariable Long postId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        postService.deletePost(postId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

}
