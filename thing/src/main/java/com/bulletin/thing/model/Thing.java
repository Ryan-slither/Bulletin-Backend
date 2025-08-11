package com.bulletin.thing.model;

import jakarta.persistence.*;

@Entity
public class Thing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private Long timeCreated;

    @Column(name = "bulletin_id")
    private Long bulletinId;

    @Column(name = "user_id")
    private Long userId;

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

    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
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
