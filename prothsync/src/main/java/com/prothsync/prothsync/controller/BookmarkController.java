package com.prothsync.prothsync.controller;

import com.prothsync.prothsync.controller.docs.BookmarkControllerDocs;
import com.prothsync.prothsync.dto.BookmarkResponseDTO;
import com.prothsync.prothsync.dto.PostResponseDTO;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.security.CustomUserDetails;
import com.prothsync.prothsync.service.BookmarkService;
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
@RequestMapping("/api")
public class BookmarkController implements BookmarkControllerDocs {

    private final BookmarkService bookmarkService;

    @PostMapping("/posts/{postId}/bookmarks")
    public ResponseEntity<BookmarkResponseDTO> toggleBookmark(
        @PathVariable Long postId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        BookmarkResponseDTO response = bookmarkService.toggleBookmark(
            postId, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bookmarks/me")
    public ResponseEntity<PageResponse<PostResponseDTO>> getMyBookmarks(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        Pageable pageable
    ) {
        PageResponse<PostResponseDTO> response = bookmarkService.getMyBookmarks(
            userDetails.getUserId(), pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts/{postId}/bookmarks")
    public ResponseEntity<Boolean> isBookmarked(
        @PathVariable Long postId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        boolean bookmarked = bookmarkService.isBookmarked(
            postId, userDetails.getUserId());
        return ResponseEntity.ok(bookmarked);
    }
}