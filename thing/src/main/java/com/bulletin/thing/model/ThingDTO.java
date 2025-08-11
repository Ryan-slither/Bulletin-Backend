package com.bulletin.thing.model;

public record ThingDTO (Long id, String content, Long bulletinId, Long userId, Long timeCreated) {
}
