package com.bulletin.userlike.socket;

public class DeleteUserLikeResponse {

    Long thingId;

    public DeleteUserLikeResponse() {
    }

    public DeleteUserLikeResponse(Long thingId) {
        this.thingId = thingId;
    }

    public Long getThingId() {
        return thingId;
    }

    public void setThingId(Long thingId) {
        this.thingId = thingId;
    }

}
