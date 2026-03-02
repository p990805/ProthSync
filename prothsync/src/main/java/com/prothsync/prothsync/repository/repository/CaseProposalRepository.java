package com.prothsync.prothsync.repository.repository;

import com.prothsync.prothsync.entity.caserequest.CaseProposal;
import com.prothsync.prothsync.entity.caserequest.ProposalStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CaseProposalRepository {

    CaseProposal save(CaseProposal proposal);

    Optional<CaseProposal> findById(Long proposalId);

    void softDelete(CaseProposal proposal, Long userId);

    Page<CaseProposal> findAllByCaseRequestId(Long caseRequestId, Pageable pageable);

    Page<CaseProposal> findAllByProposerId(Long proposerId, Pageable pageable);

    boolean existsByCaseRequestIdAndProposerId(Long caseRequestId, Long proposerId);

    List<CaseProposal> findAllByCaseRequestIdAndStatus(Long caseRequestId, ProposalStatus status);

    long countByCaseRequestId(Long caseRequestId);

    void softDeleteAllByCaseRequestId(Long caseRequestId, Long userId);
}