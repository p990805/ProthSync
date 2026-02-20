package com.prothsync.prothsync.controller;

import com.prothsync.prothsync.controller.docs.FeedControllerDocs;
import com.prothsync.prothsync.dto.PostResponseDTO;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.security.CustomUserDetails;
import com.prothsync.prothsync.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feed")
public class FeedController implements FeedControllerDocs {

    private final FeedService feedService;

    @GetMapping
    public ResponseEntity<PageResponse<PostResponseDTO>> getFollowingFeed(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        Pageable pageable
    ) {
        PageResponse<PostResponseDTO> response = feedService.getFollowingFeed(
            userDetails.getUserId(), pageable);
        return ResponseEntity.ok(response);
    }
}