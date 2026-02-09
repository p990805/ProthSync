package com.prothsync.prothsync.controller;

import com.prothsync.prothsync.controller.docs.CommentControllerDocs;
import com.prothsync.prothsync.dto.CommentCreateRequestDTO;
import com.prothsync.prothsync.dto.CommentResponseDTO;
import com.prothsync.prothsync.dto.CommentUpdateRequestDTO;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.security.CustomUserDetails;
import com.prothsync.prothsync.service.CommentService;
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
@RequestMapping("/api")
public class CommentController implements CommentControllerDocs {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponseDTO> createComment(
        @PathVariable Long postId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody CommentCreateRequestDTO request
    ) {
        CommentResponseDTO response = commentService.createComment(
            postId, userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<PageResponse<CommentResponseDTO>> getComments(
        @PathVariable Long postId,
        Pageable pageable
    ) {
        PageResponse<CommentResponseDTO> response = commentService.getComments(postId, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(
        @PathVariable Long commentId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody CommentUpdateRequestDTO request
    ) {
        CommentResponseDTO response = commentService.updateComment(
            commentId, userDetails.getUserId(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
        @PathVariable Long commentId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        commentService.deleteComment(commentId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }
}