package com.prothsync.prothsync.controller;

import com.prothsync.prothsync.controller.docs.CaseRequestControllerDocs;
import com.prothsync.prothsync.dto.CaseRequestCreateDTO;
import com.prothsync.prothsync.dto.CaseRequestResponseDTO;
import com.prothsync.prothsync.dto.CaseRequestSummaryDTO;
import com.prothsync.prothsync.dto.CaseRequestUpdateDTO;
import com.prothsync.prothsync.dto.NearbyCaseResponseDTO;
import com.prothsync.prothsync.entity.caserequest.CaseCategory;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.security.CustomUserDetails;
import com.prothsync.prothsync.service.CaseRequestService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cases")
public class CaseRequestController implements CaseRequestControllerDocs {

    private final CaseRequestService caseRequestService;

    @PostMapping
    public ResponseEntity<CaseRequestResponseDTO> createCaseRequest(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody CaseRequestCreateDTO request
    ) {
        CaseRequestResponseDTO response = caseRequestService.createCaseRequest(
            userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{caseRequestId}")
    public ResponseEntity<CaseRequestResponseDTO> getCaseRequest(
        @PathVariable Long caseRequestId
    ) {
        CaseRequestResponseDTO response = caseRequestService.getCaseRequest(caseRequestId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<CaseRequestSummaryDTO>> getOpenCaseRequests(
        Pageable pageable
    ) {
        PageResponse<CaseRequestSummaryDTO> response = caseRequestService.getOpenCaseRequests(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<PageResponse<CaseRequestSummaryDTO>> getCaseRequestsByCategory(
        @PathVariable CaseCategory category,
        Pageable pageable
    ) {
        PageResponse<CaseRequestSummaryDTO> response = caseRequestService.getCaseRequestsByCategory(
            category, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<PageResponse<CaseRequestSummaryDTO>> getMyCaseRequests(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        Pageable pageable
    ) {
        PageResponse<CaseRequestSummaryDTO> response = caseRequestService.getMyCaseRequests(
            userDetails.getUserId(), pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{caseRequestId}")
    public ResponseEntity<CaseRequestResponseDTO> updateCaseRequest(
        @PathVariable Long caseRequestId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody CaseRequestUpdateDTO request
    ) {
        CaseRequestResponseDTO response = caseRequestService.updateCaseRequest(
            caseRequestId, userDetails.getUserId(), request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{caseRequestId}/cancel")
    public ResponseEntity<Void> cancelCaseRequest(
        @PathVariable Long caseRequestId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        caseRequestService.cancelCaseRequest(caseRequestId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{caseRequestId}")
    public ResponseEntity<Void> deleteCaseRequest(
        @PathVariable Long caseRequestId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        caseRequestService.deleteCaseRequest(caseRequestId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<NearbyCaseResponseDTO>> getNearbyCases(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(required = false) Double radiusKm,
        @RequestParam(required = false) Integer limit
    ) {
        List<NearbyCaseResponseDTO> response = caseRequestService.findNearbyCases(
            userDetails.getUserId(), radiusKm, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/nearby/{category}")
    public ResponseEntity<List<NearbyCaseResponseDTO>> getNearbyCasesByCategory(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable CaseCategory category,
        @RequestParam(required = false) Double radiusKm,
        @RequestParam(required = false) Integer limit
    ) {
        List<NearbyCaseResponseDTO> response = caseRequestService.findNearbyCasesByCategory(
            userDetails.getUserId(), category, radiusKm, limit);
        return ResponseEntity.ok(response);
    }
}