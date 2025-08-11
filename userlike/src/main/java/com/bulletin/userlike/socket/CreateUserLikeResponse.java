package com.bulletin.userlike.socket;

public class CreateUserLikeResponse {

    Long id;

    Long userId;

    Long thingId;

    Long bulletinId;

    public CreateUserLikeResponse() {
    }

    public CreateUserLikeResponse(Long id, Long userId, Long thingId, Long bulletinId) {
        this.id = id;
        this.userId = userId;
        this.thingId = thingId;
        this.bulletinId = bulletinId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBulletinId() {
        return bulletinId;
    }

    public void setBulletinId(Long bulletinId) {
        this.bulletinId = bulletinId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getThingId() {
        return thingId;
    }

    public void setThingId(Long bulletinId) {
        this.thingId = bulletinId;
    }

}
