package com.bulletin.thing.socket;

public class DeleteThingMessage {

    Long id;

    public DeleteThingMessage() {
    }

    public DeleteThingMessage(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
