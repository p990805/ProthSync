package com.prothsync.prothsync.controller;

import com.prothsync.prothsync.controller.docs.FollowControllerDocs;
import com.prothsync.prothsync.dto.FollowResponseDTO;
import com.prothsync.prothsync.dto.FollowUserResponseDTO;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.security.CustomUserDetails;
import com.prothsync.prothsync.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{userId}/follow")
public class FollowController implements FollowControllerDocs {

    private final FollowService followService;

    @PostMapping
    public ResponseEntity<FollowResponseDTO> toggleFollow(
        @PathVariable Long userId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        FollowResponseDTO response = followService.toggleFollow(
            userDetails.getUserId(), userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<Boolean> isFollowing(
        @PathVariable Long userId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        boolean following = followService.isFollowing(
            userDetails.getUserId(), userId);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/followers")
    public ResponseEntity<PageResponse<FollowUserResponseDTO>> getFollowers(
        @PathVariable Long userId,
        Pageable pageable
    ) {
        PageResponse<FollowUserResponseDTO> response = followService.getFollowers(
            userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/followings")
    public ResponseEntity<PageResponse<FollowUserResponseDTO>> getFollowings(
        @PathVariable Long userId,
        Pageable pageable
    ) {
        PageResponse<FollowUserResponseDTO> response = followService.getFollowings(
            userId, pageable);
        return ResponseEntity.ok(response);
    }
}