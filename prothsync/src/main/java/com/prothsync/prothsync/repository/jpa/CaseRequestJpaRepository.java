package com.prothsync.prothsync.repository.jpa;

import com.prothsync.prothsync.entity.caserequest.CaseCategory;
import com.prothsync.prothsync.entity.caserequest.CaseRequest;
import com.prothsync.prothsync.entity.caserequest.CaseStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CaseRequestJpaRepository extends JpaRepository<CaseRequest, Long> {

    Page<CaseRequest> findAllByStatus(CaseStatus status, Pageable pageable);

    Page<CaseRequest> findAllByClientId(Long clientId, Pageable pageable);

    Page<CaseRequest> findAllByCategory(CaseCategory category, Pageable pageable);

    Page<CaseRequest> findAllByStatusAndCategory(CaseStatus status, CaseCategory category, Pageable pageable);

    /**
     * Haversine 공식을 이용한 주변 OPEN 상태 의뢰 검색
     */
    @Query(value = """
        SELECT cr.* FROM case_requests cr
        WHERE cr.latitude IS NOT NULL
          AND cr.longitude IS NOT NULL
          AND cr.client_id != :excludeUserId
          AND cr.status = 'OPEN'
          AND cr.deleted_at IS NULL
          AND (
            6371 * acos(
              LEAST(1.0, GREATEST(-1.0,
                cos(radians(:lat)) * cos(radians(cr.latitude))
                * cos(radians(cr.longitude) - radians(:lng))
                + sin(radians(:lat)) * sin(radians(cr.latitude))
              ))
            )
          ) < :radiusKm
        ORDER BY (
            6371 * acos(
              LEAST(1.0, GREATEST(-1.0,
                cos(radians(:lat)) * cos(radians(cr.latitude))
                * cos(radians(cr.longitude) - radians(:lng))
                + sin(radians(:lat)) * sin(radians(cr.latitude))
              ))
            )
          ) ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<CaseRequest> findNearbyCases(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("radiusKm") double radiusKm,
        @Param("excludeUserId") Long excludeUserId,
        @Param("limit") int limit
    );

    /**
     * 카테고리 필터가 추가된 주변 OPEN 의뢰 검색
     */
    @Query(value = """
        SELECT cr.* FROM case_requests cr
        WHERE cr.latitude IS NOT NULL
          AND cr.longitude IS NOT NULL
          AND cr.client_id != :excludeUserId
          AND cr.status = 'OPEN'
          AND cr.category = :category
          AND cr.deleted_at IS NULL
          AND (
            6371 * acos(
              LEAST(1.0, GREATEST(-1.0,
                cos(radians(:lat)) * cos(radians(cr.latitude))
                * cos(radians(cr.longitude) - radians(:lng))
                + sin(radians(:lat)) * sin(radians(cr.latitude))
              ))
            )
          ) < :radiusKm
        ORDER BY (
            6371 * acos(
              LEAST(1.0, GREATEST(-1.0,
                cos(radians(:lat)) * cos(radians(cr.latitude))
                * cos(radians(cr.longitude) - radians(:lng))
                + sin(radians(:lat)) * sin(radians(cr.latitude))
              ))
            )
          ) ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<CaseRequest> findNearbyCasesByCategory(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("radiusKm") double radiusKm,
        @Param("excludeUserId") Long excludeUserId,
        @Param("category") String category,
        @Param("limit") int limit
    );
}