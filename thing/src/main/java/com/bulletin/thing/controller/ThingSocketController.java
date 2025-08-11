package com.bulletin.thing.controller;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.bulletin.thing.model.ThingDTO;
import com.bulletin.thing.service.ThingService;
import com.bulletin.thing.socket.CreateThingMessage;
import com.bulletin.thing.socket.CreateThingResponse;
import com.bulletin.thing.socket.DeleteThingMessage;
import com.bulletin.thing.socket.UpdateThingMessage;

@Controller
public class ThingSocketController {

    private final ThingService thingService;

    public ThingSocketController(ThingService thingService) {
        this.thingService = thingService;
    }

    @MessageMapping("/create/{bulletinId}")
    @SendTo("/topic/{bulletinId}")
    public CreateThingResponse createThing(CreateThingMessage createThingMessage,
            @DestinationVariable Long bulletinId, @Header("simpSessionAttributes") Map<String, Object> sessionAttrs) {
        Long userId = Long.parseLong(sessionAttrs.get("userId").toString());
        if (userId == null || userId != createThingMessage.getUserId()) {
            throw new IllegalStateException("Cannot Create Entity: Unauthorized");
        }

        ThingDTO createdThing = thingService.createThing(new ThingDTO(null, createThingMessage.getContent(),
                bulletinId, createThingMessage.getUserId(), null));

        return new CreateThingResponse(createdThing.id(), createdThing.content(), createdThing.bulletinId(),
                createdThing.userId(), createdThing.timeCreated());
    }

    @MessageMapping("/update/{bulletinId}")
    @SendTo("/topic/{bulletinId}")
    public CreateThingResponse updateThing(UpdateThingMessage updateThingMessage,
            @DestinationVariable Long bulletinId,
            @Header("simpSessionAttributes") Map<String, Object> sessionAttrs) {
        Optional<ThingDTO> thing = thingService.getThingById(updateThingMessage.getId());
        if (thing.isEmpty()) {
            throw new IllegalStateException("Cannot Update Entity That Does Not Exist");
        }

        Long userId = Long.parseLong(sessionAttrs.get("userId").toString());
        if (userId == null || userId != thing.get().userId()) {
            throw new IllegalStateException("Cannot Update Entity: Unauthorized");
        }

        ThingDTO updatedThing = thingService.updateThing(updateThingMessage.getId(),
                new ThingDTO(null, updateThingMessage.getContent(), null, null, Instant.now().getEpochSecond()));
        return new CreateThingResponse(updatedThing.id(), updatedThing.content(), bulletinId,
                updatedThing.userId(), updatedThing.timeCreated());
    }

    @MessageMapping("/delete/{bulletinId}")
    @SendTo("/topic/{bulletinId}")
    public DeleteThingMessage deleteThing(DeleteThingMessage deleteThingMessage, @DestinationVariable Long bulletinId,
            @Header("simpSessionAttributes") Map<String, Object> sessionAttrs) {

        Optional<ThingDTO> thing = thingService.getThingById(deleteThingMessage.getId());
        Long userId = Long.parseLong(sessionAttrs.get("userId").toString());

        if (userId == null || !thing.get().userId().equals(userId)) {

            throw new IllegalStateException("Cannot Delete Entity: Unauthorized");

        }

        thingService.deleteThing(deleteThingMessage.getId(), userId);
        return new DeleteThingMessage(thing.get().id());

    }

}
