package com.prothsync.prothsync.service;

import com.prothsync.prothsync.dto.CaseProposalCreateDTO;
import com.prothsync.prothsync.dto.CaseProposalResponseDTO;
import com.prothsync.prothsync.entity.caserequest.CaseProposal;
import com.prothsync.prothsync.entity.caserequest.CaseRequest;
import com.prothsync.prothsync.entity.caserequest.ProposalStatus;
import com.prothsync.prothsync.entity.notification.NotificationType;
import com.prothsync.prothsync.entity.notification.ReferenceType;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.CaseErrorCode;
import com.prothsync.prothsync.exception.UserErrorCode;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.repository.repository.CaseProposalRepository;
import com.prothsync.prothsync.repository.repository.CaseRequestRepository;
import com.prothsync.prothsync.repository.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CaseProposalService {

    private final CaseProposalRepository caseProposalRepository;
    private final CaseRequestRepository caseRequestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public CaseProposalResponseDTO createProposal(
        Long caseRequestId, Long proposerId, CaseProposalCreateDTO request) {

        validateUserExists(proposerId);
        CaseRequest caseRequest = findCaseRequestOrThrow(caseRequestId);

        validateNotOwnCase(caseRequest, proposerId);
        validateCaseIsOpen(caseRequest);
        validateNoDuplicateProposal(caseRequestId, proposerId);

        CaseProposal proposal = CaseProposal.create(
            caseRequestId,
            proposerId,
            request.message(),
            request.estimatedPrice(),
            request.estimatedDays()
        );

        CaseProposal saved = caseProposalRepository.save(proposal);

        caseRequest.incrementProposalCount();
        caseRequestRepository.save(caseRequest);

        notificationService.send(
            NotificationType.PROPOSAL_RECEIVED, proposerId, caseRequest.getClientId(),
            caseRequestId, ReferenceType.CASE_REQUEST);

        return CaseProposalResponseDTO.from(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<CaseProposalResponseDTO> getProposalsByCaseRequest(
        Long caseRequestId, Long userId, Pageable pageable) {

        CaseRequest caseRequest = findCaseRequestOrThrow(caseRequestId);
        validateCaseOwner(caseRequest, userId);

        Page<CaseProposal> page = caseProposalRepository.findAllByCaseRequestId(
            caseRequestId, pageable);

        return toPageResponse(page);
    }

    @Transactional(readOnly = true)
    public PageResponse<CaseProposalResponseDTO> getMyProposals(Long proposerId, Pageable pageable) {
        Page<CaseProposal> page = caseProposalRepository.findAllByProposerId(proposerId, pageable);
        return toPageResponse(page);
    }

    @Transactional
    public CaseProposalResponseDTO acceptProposal(Long proposalId, Long userId) {
        CaseProposal proposal = findProposalOrThrow(proposalId);
        CaseRequest caseRequest = findCaseRequestOrThrow(proposal.getCaseRequestId());

        validateCaseOwner(caseRequest, userId);
        validateCaseIsOpen(caseRequest);

        proposal.accept();
        caseProposalRepository.save(proposal);

        rejectOtherProposals(caseRequest.getCaseRequestId(), proposalId);

        caseRequest.startProgress();
        caseRequestRepository.save(caseRequest);

        notificationService.send(
            NotificationType.PROPOSAL_ACCEPTED, userId, proposal.getProposerId(),
            proposalId, ReferenceType.CASE_PROPOSAL);

        return CaseProposalResponseDTO.from(proposal);
    }

    @Transactional
    public CaseProposalResponseDTO rejectProposal(Long proposalId, Long userId) {
        CaseProposal proposal = findProposalOrThrow(proposalId);
        CaseRequest caseRequest = findCaseRequestOrThrow(proposal.getCaseRequestId());

        validateCaseOwner(caseRequest, userId);

        proposal.reject();
        caseProposalRepository.save(proposal);

        notificationService.send(
            NotificationType.PROPOSAL_REJECTED, userId, proposal.getProposerId(),
            proposalId, ReferenceType.CASE_PROPOSAL);

        return CaseProposalResponseDTO.from(proposal);
    }

    @Transactional
    public void deleteProposal(Long proposalId, Long userId) {
        CaseProposal proposal = findProposalOrThrow(proposalId);
        validateProposalOwner(proposal, userId);
        validateProposalIsPending(proposal);

        CaseRequest caseRequest = findCaseRequestOrThrow(proposal.getCaseRequestId());
        caseRequest.decrementProposalCount();
        caseRequestRepository.save(caseRequest);

        caseProposalRepository.delete(proposal);
    }

    @Transactional
    public void completeCaseRequest(Long caseRequestId, Long userId) {
        CaseRequest caseRequest = findCaseRequestOrThrow(caseRequestId);
        validateCaseOwner(caseRequest, userId);

        caseRequest.complete();
        caseRequestRepository.save(caseRequest);

        caseProposalRepository
            .findAllByCaseRequestIdAndStatus(caseRequestId, ProposalStatus.ACCEPTED)
            .forEach(proposal -> notificationService.send(
                NotificationType.CASE_COMPLETED, userId, proposal.getProposerId(),
                caseRequestId, ReferenceType.CASE_REQUEST));
    }

    private CaseRequest findCaseRequestOrThrow(Long caseRequestId) {
        return caseRequestRepository.findById(caseRequestId)
            .orElseThrow(() -> new BusinessException(CaseErrorCode.CASE_NOT_FOUND));
    }

    private CaseProposal findProposalOrThrow(Long proposalId) {
        return caseProposalRepository.findById(proposalId)
            .orElseThrow(() -> new BusinessException(CaseErrorCode.PROPOSAL_NOT_FOUND));
    }

    private void validateUserExists(Long userId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    private void validateNotOwnCase(CaseRequest caseRequest, Long proposerId) {
        if (caseRequest.isOwner(proposerId)) {
            throw new BusinessException(CaseErrorCode.PROPOSAL_OWN_CASE);
        }
    }

    private void validateCaseIsOpen(CaseRequest caseRequest) {
        if (!caseRequest.isOpen()) {
            throw new BusinessException(CaseErrorCode.CASE_NOT_OPEN);
        }
    }

    private void validateCaseOwner(CaseRequest caseRequest, Long userId) {
        if (!caseRequest.isOwner(userId)) {
            throw new BusinessException(CaseErrorCode.CASE_ACCESS_DENIED);
        }
    }

    private void validateNoDuplicateProposal(Long caseRequestId, Long proposerId) {
        if (caseProposalRepository.existsByCaseRequestIdAndProposerId(caseRequestId, proposerId)) {
            throw new BusinessException(CaseErrorCode.PROPOSAL_ALREADY_EXISTS);
        }
    }

    private void validateProposalOwner(CaseProposal proposal, Long userId) {
        if (!proposal.isOwnedBy(userId)) {
            throw new BusinessException(CaseErrorCode.PROPOSAL_ACCESS_DENIED);
        }
    }

    private void validateProposalIsPending(CaseProposal proposal) {
        if (!proposal.isPending()) {
            throw new BusinessException(CaseErrorCode.PROPOSAL_ALREADY_DECIDED);
        }
    }

    private void rejectOtherProposals(Long caseRequestId, Long acceptedProposalId) {
        List<CaseProposal> pendingProposals = caseProposalRepository
            .findAllByCaseRequestIdAndStatus(caseRequestId, ProposalStatus.PENDING);

        for (CaseProposal other : pendingProposals) {
            if (!other.getProposalId().equals(acceptedProposalId)) {
                other.reject();
                caseProposalRepository.save(other);
            }
        }
    }

    private PageResponse<CaseProposalResponseDTO> toPageResponse(Page<CaseProposal> page) {
        List<CaseProposalResponseDTO> content = page.getContent().stream()
            .map(CaseProposalResponseDTO::from)
            .toList();
        return PageResponse.of(content, page);
    }
}