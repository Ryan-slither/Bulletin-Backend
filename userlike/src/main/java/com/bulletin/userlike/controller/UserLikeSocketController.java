package com.bulletin.userlike.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.bulletin.userlike.model.UserLikeDTO;
import com.bulletin.userlike.service.UserLikeService;
import com.bulletin.userlike.socket.CreateUserLikeMessage;
import com.bulletin.userlike.socket.CreateUserLikeResponse;
import com.bulletin.userlike.socket.DeleteUserLikeMessage;
import com.bulletin.userlike.socket.DeleteUserLikeResponse;

@Controller
public class UserLikeSocketController {

    private final UserLikeService userLikeService;

    public UserLikeSocketController(UserLikeService userLikeService) {
        this.userLikeService = userLikeService;
    }

    @MessageMapping("/create/{bulletinId}")
    @SendTo("/topic/{bulletinId}")
    public CreateUserLikeResponse createUserLike(CreateUserLikeMessage createUserLikeMessage,
            @DestinationVariable Long bulletinId, @Header("simpSessionAttributes") Map<String, Object> sessionAttrs) {

        Long userId = Long.parseLong(sessionAttrs.get("userId").toString());
        if (userId == null || userId != createUserLikeMessage.getUserId()) {
            throw new IllegalStateException("Cannot Create Entity: Unauthorized");
        }

        UserLikeDTO createdUserLike = userLikeService.createLike(new UserLikeDTO(null,
                createUserLikeMessage.getThingId(), createUserLikeMessage.getUserId(), bulletinId));

        return new CreateUserLikeResponse(createdUserLike.id(), createdUserLike.userId(), createdUserLike.thingId(),
                bulletinId);

    }

    @MessageMapping("/delete/{bulletinId}")
    @SendTo("/topic/{bulletinId}")
    public DeleteUserLikeResponse deleteUserLike(DeleteUserLikeMessage deleteUserLikeMessage,
            @DestinationVariable Long bulletinId,
            @Header("simpSessionAttributes") Map<String, Object> sessionAttrs) {

        Optional<UserLikeDTO> userLike = userLikeService.getLikeByUserAndThing(deleteUserLikeMessage.getUserId(),
                deleteUserLikeMessage.getThingId());
        Long userId = Long.parseLong(sessionAttrs.get("userId").toString());

        if (userId == null || !userLike.get().userId().equals(userId)) {

            throw new IllegalStateException("Cannot Update Entity: Unauthorized");

        }

        userLikeService.deleteLikeById(userLike.get().id());
        return new DeleteUserLikeResponse(userLike.get().thingId());

    }

}
