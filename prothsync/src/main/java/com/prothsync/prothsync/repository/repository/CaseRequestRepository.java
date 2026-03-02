package com.prothsync.prothsync.repository.repository;

import com.prothsync.prothsync.entity.caserequest.CaseCategory;
import com.prothsync.prothsync.entity.caserequest.CaseRequest;
import com.prothsync.prothsync.entity.caserequest.CaseStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CaseRequestRepository {

    CaseRequest save(CaseRequest caseRequest);

    Optional<CaseRequest> findById(Long caseRequestId);

    void softDelete(CaseRequest caseRequest, Long userId);

    Page<CaseRequest> findAllByStatus(CaseStatus status, Pageable pageable);

    Page<CaseRequest> findAllByClientId(Long clientId, Pageable pageable);

    Page<CaseRequest> findAllByCategory(CaseCategory category, Pageable pageable);

    Page<CaseRequest> findAllByStatusAndCategory(CaseStatus status, CaseCategory category, Pageable pageable);

    List<CaseRequest> findNearbyCases(double lat, double lng, double radiusKm,
        Long excludeUserId, int limit);

    List<CaseRequest> findNearbyCasesByCategory(double lat, double lng, double radiusKm,
        Long excludeUserId, CaseCategory category, int limit);

    long count();
}