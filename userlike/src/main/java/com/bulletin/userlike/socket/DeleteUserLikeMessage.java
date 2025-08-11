package com.bulletin.userlike.socket;

public class DeleteUserLikeMessage {

    Long thingId;

    Long userId;

    public DeleteUserLikeMessage(Long thingId, Long userId) {
        this.thingId = thingId;
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public DeleteUserLikeMessage() {
    }

    public Long getThingId() {
        return thingId;
    }

    public void setThingId(Long thingId) {
        this.thingId = thingId;
    }

}
