package com.prothsync.prothsync.repository.jpa;

import com.prothsync.prothsync.entity.caserequest.CaseProposal;
import com.prothsync.prothsync.entity.caserequest.ProposalStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseProposalJpaRepository extends JpaRepository<CaseProposal, Long> {

    Page<CaseProposal> findAllByCaseRequestId(Long caseRequestId, Pageable pageable);

    Page<CaseProposal> findAllByProposerId(Long proposerId, Pageable pageable);

    boolean existsByCaseRequestIdAndProposerId(Long caseRequestId, Long proposerId);

    List<CaseProposal> findAllByCaseRequestIdAndStatus(Long caseRequestId, ProposalStatus status);

    long countByCaseRequestId(Long caseRequestId);

    void deleteAllByCaseRequestId(Long caseRequestId);
}