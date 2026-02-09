package com.prothsync.prothsync.controller;

import com.prothsync.prothsync.controller.docs.HashtagControllerDocs;
import com.prothsync.prothsync.dto.HashtagResponseDTO;
import com.prothsync.prothsync.service.HashtagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hashtags")
public class HashtagController implements HashtagControllerDocs {

    private final HashtagService hashtagService;

    @GetMapping("/trending")
    public ResponseEntity<List<HashtagResponseDTO>> getTrendingHashtags(
        @RequestParam(defaultValue = "10") int limit
    ) {
        List<HashtagResponseDTO> response = hashtagService.getTrendingHashtags(limit);
        return ResponseEntity.ok(response);
    }
}