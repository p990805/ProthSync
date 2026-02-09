package com.prothsync.prothsync.controller;

import com.prothsync.prothsync.controller.docs.LikeControllerDocs;
import com.prothsync.prothsync.dto.LikeResponseDTO;
import com.prothsync.prothsync.security.CustomUserDetails;
import com.prothsync.prothsync.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/likes")
public class LikeController implements LikeControllerDocs {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<LikeResponseDTO> toggleLike(
        @PathVariable Long postId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        LikeResponseDTO response = likeService.toggleLike(postId, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Boolean> isLiked(
        @PathVariable Long postId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        boolean liked = likeService.isLiked(postId, userDetails.getUserId());
        return ResponseEntity.ok(liked);
    }
}