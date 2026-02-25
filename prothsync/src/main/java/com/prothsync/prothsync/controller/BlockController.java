package com.prothsync.prothsync.controller;

import com.prothsync.prothsync.controller.docs.BlockControllerDocs;
import com.prothsync.prothsync.dto.BlockResponseDTO;
import com.prothsync.prothsync.dto.BlockedUserResponseDTO;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.security.CustomUserDetails;
import com.prothsync.prothsync.service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/blocks")
public class BlockController implements BlockControllerDocs {

    private final BlockService blockService;

    @PostMapping("/{userId}")
    public ResponseEntity<BlockResponseDTO> block(
        @PathVariable Long userId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        BlockResponseDTO response = blockService.block(userDetails.getUserId(), userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<BlockResponseDTO> unblock(
        @PathVariable Long userId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        BlockResponseDTO response = blockService.unblock(userDetails.getUserId(), userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<BlockedUserResponseDTO>> getBlockedUsers(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        Pageable pageable
    ) {
        PageResponse<BlockedUserResponseDTO> response = blockService.getBlockedUsers(
            userDetails.getUserId(), pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/status")
    public ResponseEntity<Boolean> isBlocked(
        @PathVariable Long userId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        boolean blocked = blockService.isBlocked(userDetails.getUserId(), userId);
        return ResponseEntity.ok(blocked);
    }
}