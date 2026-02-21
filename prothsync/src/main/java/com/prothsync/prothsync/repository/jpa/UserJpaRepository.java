package com.prothsync.prothsync.repository.jpa;

import com.prothsync.prothsync.entity.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    boolean existsByUserName(String username);

    boolean existsByNickName(String nickName);

    boolean existsByEmail(String email);

    Optional<User> findByUserName(String userName);

    Optional<User> findByNickName(String nickName);

    Optional<User> findByEmail(String email);

    /**
     * Haversine 공식을 이용한 주변 사용자 검색
     * 사업체(치과, 기공소)만 검색되도록 userType IN 조건 포함
     */
    @Query(value = """
        SELECT u.* FROM users u
        WHERE u.latitude IS NOT NULL
          AND u.longitude IS NOT NULL
          AND u.user_id != :excludeUserId
          AND u.user_type IN (:searchableTypes)
          AND (
            6371 * acos(
              LEAST(1.0, GREATEST(-1.0,
                cos(radians(:lat)) * cos(radians(u.latitude))
                * cos(radians(u.longitude) - radians(:lng))
                + sin(radians(:lat)) * sin(radians(u.latitude))
              ))
            )
          ) < :radiusKm
        ORDER BY (
            6371 * acos(
              LEAST(1.0, GREATEST(-1.0,
                cos(radians(:lat)) * cos(radians(u.latitude))
                * cos(radians(u.longitude) - radians(:lng))
                + sin(radians(:lat)) * sin(radians(u.latitude))
              ))
            )
          ) ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<User> findNearbyUsers(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("radiusKm") double radiusKm,
        @Param("excludeUserId") Long excludeUserId,
        @Param("searchableTypes") List<String> searchableTypes,
        @Param("limit") int limit
    );

    /**
     * 특정 사용자 유형으로 필터링한 주변 검색
     * (치과 또는 기공소 중 하나만 선택해서 검색)
     */
    @Query(value = """
        SELECT u.* FROM users u
        WHERE u.latitude IS NOT NULL
          AND u.longitude IS NOT NULL
          AND u.user_id != :excludeUserId
          AND u.user_type = :userType
          AND (
            6371 * acos(
              LEAST(1.0, GREATEST(-1.0,
                cos(radians(:lat)) * cos(radians(u.latitude))
                * cos(radians(u.longitude) - radians(:lng))
                + sin(radians(:lat)) * sin(radians(u.latitude))
              ))
            )
          ) < :radiusKm
        ORDER BY (
            6371 * acos(
              LEAST(1.0, GREATEST(-1.0,
                cos(radians(:lat)) * cos(radians(u.latitude))
                * cos(radians(u.longitude) - radians(:lng))
                + sin(radians(:lat)) * sin(radians(u.latitude))
              ))
            )
          ) ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<User> findNearbyUsersByType(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("radiusKm") double radiusKm,
        @Param("excludeUserId") Long excludeUserId,
        @Param("userType") String userType,
        @Param("limit") int limit
    );

    Page<User> findByNickNameContainingIgnoreCase(String keyword, Pageable pageable);
}