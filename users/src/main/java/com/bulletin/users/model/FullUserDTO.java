package com.bulletin.users.model;

public record FullUserDTO(
        Long id,
        String email,
        String password,
        Long timeCreated,
        Long verificationCode,
        boolean enabled,
        Long passwordCode) {
}