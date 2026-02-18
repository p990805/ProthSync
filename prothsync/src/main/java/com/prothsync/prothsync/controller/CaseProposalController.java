package com.prothsync.prothsync.controller;

import com.prothsync.prothsync.controller.docs.CaseProposalControllerDocs;
import com.prothsync.prothsync.dto.CaseProposalCreateDTO;
import com.prothsync.prothsync.dto.CaseProposalResponseDTO;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.security.CustomUserDetails;
import com.prothsync.prothsync.service.CaseProposalService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CaseProposalController implements CaseProposalControllerDocs {

    private final CaseProposalService caseProposalService;

    @PostMapping("/cases/{caseRequestId}/proposals")
    public ResponseEntity<CaseProposalResponseDTO> createProposal(
        @PathVariable Long caseRequestId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody CaseProposalCreateDTO request
    ) {
        CaseProposalResponseDTO response = caseProposalService.createProposal(
            caseRequestId, userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/cases/{caseRequestId}/proposals")
    public ResponseEntity<PageResponse<CaseProposalResponseDTO>> getProposalsByCaseRequest(
        @PathVariable Long caseRequestId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        Pageable pageable
    ) {
        PageResponse<CaseProposalResponseDTO> response = caseProposalService.getProposalsByCaseRequest(
            caseRequestId, userDetails.getUserId(), pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/proposals/me")
    public ResponseEntity<PageResponse<CaseProposalResponseDTO>> getMyProposals(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        Pageable pageable
    ) {
        PageResponse<CaseProposalResponseDTO> response = caseProposalService.getMyProposals(
            userDetails.getUserId(), pageable);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/proposals/{proposalId}/accept")
    public ResponseEntity<CaseProposalResponseDTO> acceptProposal(
        @PathVariable Long proposalId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CaseProposalResponseDTO response = caseProposalService.acceptProposal(
            proposalId, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/proposals/{proposalId}/reject")
    public ResponseEntity<CaseProposalResponseDTO> rejectProposal(
        @PathVariable Long proposalId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CaseProposalResponseDTO response = caseProposalService.rejectProposal(
            proposalId, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/proposals/{proposalId}")
    public ResponseEntity<Void> deleteProposal(
        @PathVariable Long proposalId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        caseProposalService.deleteProposal(proposalId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/cases/{caseRequestId}/complete")
    public ResponseEntity<Void> completeCaseRequest(
        @PathVariable Long caseRequestId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        caseProposalService.completeCaseRequest(caseRequestId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }
}