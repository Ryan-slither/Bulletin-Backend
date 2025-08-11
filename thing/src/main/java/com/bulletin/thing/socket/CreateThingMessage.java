package com.bulletin.thing.socket;

public class CreateThingMessage {

    private String content;

    private Long bulletinId;

    private Long userId;

    public CreateThingMessage() {
    }

    public CreateThingMessage(String content, Long bulletinId, Long userId) {
        this.content = content;
        this.bulletinId = bulletinId;
        this.userId = userId;
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

}
