package com.bulletin.userlike.socket;

public class CreateUserLikeMessage {

    Long userId;

    Long thingId;

    public CreateUserLikeMessage() {
    }

    public CreateUserLikeMessage(Long userId, Long bulletinId) {
        this.userId = userId;
        this.thingId = bulletinId;
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
