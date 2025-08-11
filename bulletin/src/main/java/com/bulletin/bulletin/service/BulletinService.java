package com.bulletin.bulletin.service;

import java.util.List;
import java.util.Optional;

import com.bulletin.bulletin.model.BulletinDTO;

public interface BulletinService {

    BulletinDTO createBulletin(BulletinDTO bulletinDTO);

    Optional<BulletinDTO> getBulletinById(Long id);

    List<BulletinDTO> getBulletinsByUser(Long userId);

    BulletinDTO updateBulletin(Long id, BulletinDTO bulletinDTO);

    Long joinBulletin(String joinCode, Long userId);

    void deleteBulletin(Long id);

}
