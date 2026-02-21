package com.prothsync.prothsync.controller;

import com.prothsync.prothsync.controller.docs.SearchControllerDocs;
import com.prothsync.prothsync.dto.HashtagResponseDTO;
import com.prothsync.prothsync.dto.PostSummaryResponseDTO;
import com.prothsync.prothsync.dto.UserSearchResponseDTO;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController implements SearchControllerDocs {

    private final SearchService searchService;

    @Override
    @GetMapping("/hashtags")
    public ResponseEntity<PageResponse<HashtagResponseDTO>> searchHashtags(
        @RequestParam String keyword,
        Pageable pageable
    ) {
        PageResponse<HashtagResponseDTO> response = searchService.searchHashtags(keyword, pageable);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/posts")
    public ResponseEntity<PageResponse<PostSummaryResponseDTO>> searchPostsByHashtag(
        @RequestParam String hashtag,
        Pageable pageable
    ) {
        PageResponse<PostSummaryResponseDTO> response = searchService.searchPostsByHashtag(hashtag, pageable);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/users")
    public ResponseEntity<PageResponse<UserSearchResponseDTO>> searchUsers(
        @RequestParam String keyword,
        Pageable pageable
    ) {
        PageResponse<UserSearchResponseDTO> response = searchService.searchUsers(keyword, pageable);
        return ResponseEntity.ok(response);
    }
}