package com.bulletin.bulletin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bulletin.bulletin.model.Bulletin;

public interface BulletinRepository extends JpaRepository<Bulletin, Long> {
    List<Bulletin> findByUserId(Long userId);

    Optional<Bulletin> findByJoinCode(String joinCode);
}
