package com.bulletin.userlike.model;

import jakarta.persistence.*;

@Entity
public class UserLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "thing_id")
    private Long thingId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "bulletin_id")
    private Long bulletinId;

    public Long getBulletinId() {
        return bulletinId;
    }

    public void setBulletinId(Long bulletinId) {
        this.bulletinId = bulletinId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getThingId() {
        return thingId;
    }

    public void setThingId(Long thingId) {
        this.thingId = thingId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
