package com.bulletin.thing.model;

public record BulletinDTO(Long id, String title, Long timeCreated, Long memberLimit, Boolean isOpen, Long userId,
        String joinCode) {
}
