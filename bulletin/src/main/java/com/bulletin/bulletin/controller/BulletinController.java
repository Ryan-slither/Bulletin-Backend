package com.bulletin.bulletin.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bulletin.bulletin.model.BulletinDTO;
import com.bulletin.bulletin.service.BulletinService;

@RestController
@RequestMapping("/api/v1/bulletin")
public class BulletinController {

    private final BulletinService bulletinService;

    public BulletinController(BulletinService bulletinService) {
        this.bulletinService = bulletinService;
    }

    @PostMapping
    public ResponseEntity<?> createBulletin(@RequestBody BulletinDTO bulletinDTO,
            @RequestHeader("X-User-Id") Long userId) {

        if (bulletinDTO.userId() != userId) {

            return new ResponseEntity<String>("Cannot Create A Bulletin For Another User", HttpStatus.UNAUTHORIZED);

        }

        return new ResponseEntity<BulletinDTO>(bulletinService.createBulletin(bulletinDTO), HttpStatus.CREATED);

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBulletinById(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {

        Optional<BulletinDTO> bulletin = bulletinService.getBulletinById(id);

        if (bulletin.get() == null || bulletin.get().userId() != userId) {

            return new ResponseEntity<String>("You Do Not Own This Bulletin", HttpStatus.UNAUTHORIZED);

        }

        return bulletin.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @GetMapping
    public ResponseEntity<?> getBulletinsByUser(@RequestParam Long userId,
            @RequestHeader("X-User-Id") Long headerUserId) {

        if (headerUserId != userId) {

            return new ResponseEntity<String>("You Cannot Access This Resource", HttpStatus.UNAUTHORIZED);

        }

        return new ResponseEntity<List<BulletinDTO>>(bulletinService.getBulletinsByUser(userId), HttpStatus.OK);

    }

    @PostMapping("/join/{joinCode}")
    public ResponseEntity<?> joinBulletin(@PathVariable String joinCode,
            @RequestHeader("X-User-Id") Long userId) {

        Long joinResponse = bulletinService.joinBulletin(joinCode, userId);

        if (joinResponse == -1) {

            return new ResponseEntity<String>("Could Not Join Bulletin", HttpStatus.NOT_FOUND);

        }

        return new ResponseEntity<Long>(joinResponse, HttpStatus.OK);

    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateBulletin(@PathVariable Long id, @RequestBody BulletinDTO bulletinDTO,
            @RequestHeader("X-User-Id") Long userId) {

        Optional<BulletinDTO> bulletin = bulletinService.getBulletinById(id);

        if (bulletin.get() == null || bulletin.get().userId() != userId) {

            return new ResponseEntity<String>("Cannot Update This Resource", HttpStatus.UNAUTHORIZED);

        }

        BulletinDTO updatedBulletin = bulletinService.updateBulletin(id, bulletinDTO);
        return ResponseEntity.ok(updatedBulletin);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBulletin(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {

        Optional<BulletinDTO> bulletin = bulletinService.getBulletinById(id);

        if (bulletin.get() == null || bulletin.get().userId() != userId) {

            return new ResponseEntity<String>("Cannot Delete This Resource", HttpStatus.UNAUTHORIZED);

        }

        bulletinService.deleteBulletin(id);
        return ResponseEntity.noContent().build();

    }
}
