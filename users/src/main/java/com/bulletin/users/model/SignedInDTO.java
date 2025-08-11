package com.bulletin.users.model;

public record SignedInDTO(String token, Long id, Long timeCreated) {
}
