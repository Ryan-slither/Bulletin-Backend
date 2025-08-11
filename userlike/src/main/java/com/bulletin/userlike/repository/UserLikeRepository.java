package com.bulletin.userlike.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bulletin.userlike.model.UserLike;
import com.bulletin.userlike.model.UserLikeCountDTO;

import jakarta.transaction.Transactional;

public interface UserLikeRepository extends JpaRepository<UserLike, Long> {
    Long countByThingId(Long thingId);

    @Query("""
            SELECT new com.bulletin.userlike.model.UserLikeCountDTO(
                thingId,
                COUNT(thingId)
            )
            FROM UserLike
            WHERE bulletinId = :bulletinId
            GROUP BY thingId
            """)
    List<UserLikeCountDTO> countByBulletin(@Param("bulletinId") Long bulletinId);

    List<UserLike> findByUserIdAndBulletinId(Long userId, Long bulletinId);

    Optional<UserLike> findByUserIdAndThingId(Long userId, Long thingId);

    @Transactional
    void deleteByUserIdAndThingId(Long userId, Long thingId);

    @Transactional
    void deleteByThingId(Long thingId);

    @Transactional
    void deleteByBulletinId(Long bulletinId);
}
