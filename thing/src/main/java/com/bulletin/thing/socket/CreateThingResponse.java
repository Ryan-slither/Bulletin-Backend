package com.bulletin.thing.socket;

public class CreateThingResponse {

    private Long id;

    private String content;

    private Long bulletinId;

    private Long userId;

    private Long timeCreated;

    public CreateThingResponse() {
    }

    public CreateThingResponse(Long id, String content, Long bulletinId, Long userId, Long timeCreated) {
        this.id = id;
        this.content = content;
        this.bulletinId = bulletinId;
        this.userId = userId;
        this.timeCreated = timeCreated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }

}
