package com.bulletin.membership.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bulletin.membership.model.Membership;

import jakarta.transaction.Transactional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    Optional<Membership> findByUserIdAndBulletinId(Long userId, Long bulletinId);

    List<Membership> findByUserId(Long userId);

    @Transactional
    void deleteByUserIdAndBulletinId(Long userId, Long bulletinId);

    @Transactional
    void deleteByBulletinId(Long bulletinId);

    Long countByBulletinId(Long bulletinId);

}
