package com.bulletin.userlike.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.bulletin.userlike.model.BulletinDTO;
import com.bulletin.userlike.model.ThingDTO;
import com.bulletin.userlike.model.UserLikeCountDTO;
import com.bulletin.userlike.model.UserLikeDTO;
import com.bulletin.userlike.service.UserLikeService;

@RestController
@RequestMapping("/api/v1/userlike")
public class UserLikeController {

    private final UserLikeService likeService;

    private final RestTemplate restTemplate;

    public UserLikeController(UserLikeService likeService, RestTemplate restTemplate) {
        this.likeService = likeService;
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public ResponseEntity<?> createLike(@RequestBody UserLikeDTO likeDTO, @RequestHeader("X-User-Id") Long userId) {

        if (likeDTO.userId() != userId) {

            return new ResponseEntity<String>("Cannot Create Like For User Id Not Yours", HttpStatus.UNAUTHORIZED);

        }

        return new ResponseEntity<UserLikeDTO>(likeService.createLike(likeDTO), HttpStatus.CREATED);

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLikeById(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {

        Optional<UserLikeDTO> like = likeService.getLikeById(id);

        if (like.isEmpty() || userId != like.get().userId()) {
            return new ResponseEntity<String>("Accessing Like Not Your Own", HttpStatus.UNAUTHORIZED);
        }

        return like.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @GetMapping("/thing")
    public Long getLikeCountByThing(@RequestParam Long thingId) {

        return likeService.getLikeCountByThing(thingId);

    }

    @GetMapping("/bulletin")
    public ResponseEntity<?> getLikeCountByBulletin(@RequestParam Long bulletinId,
            @RequestHeader("X-User-Id") Long userId) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-id", userId.toString());

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<?> response = restTemplate
                .exchange("http://membership/api/v1/membership/" + bulletinId + "/" + userId, HttpMethod.GET,
                        httpEntity, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {

            return new ResponseEntity<String>("Not Allowed To Access This Bulletin", HttpStatus.UNAUTHORIZED);

        }

        return new ResponseEntity<List<UserLikeCountDTO>>(likeService.getLikeCountByBulletin(bulletinId), HttpStatus.OK);

    }

    @GetMapping("/user/{userId}/bulletin/{bulletinId}")
    public ResponseEntity<?> getLikesByUserAndBulletin(@PathVariable Long userId, @PathVariable Long bulletinId,
            @RequestHeader("X-User-Id") Long headerUserId) {

        if (userId != headerUserId) {
            return new ResponseEntity<String>("Accessing Like Not Your Own", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<List<UserLikeDTO>>(likeService.getLikesByUserAndBulletin(userId, bulletinId),
                HttpStatus.OK);

    }

    @DeleteMapping("/user/{userId}/thing/{thingId}")
    public ResponseEntity<?> deleteLikeByUserAndThing(@PathVariable Long userId, @PathVariable Long thingId,
            @RequestHeader("X-User-Id") Long headerUserId) {

        if (userId != headerUserId) {
            return new ResponseEntity<String>("Accessing Like Not Your Own", HttpStatus.UNAUTHORIZED);
        }

        likeService.deleteLikeByUserAndThing(userId, thingId);
        return ResponseEntity.noContent().build();

    }

    @DeleteMapping("/thing")
    public ResponseEntity<?> deleteLikeByThing(@RequestParam Long thingId,
            @RequestHeader("X-User-Id") Long userId) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", userId.toString());

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<ThingDTO> response = restTemplate.exchange("http://thing/api/v1/thing/" + thingId,
                HttpMethod.GET, httpEntity,
                ThingDTO.class);

        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {

            return new ResponseEntity<String>("You Do Not Own This Resource", HttpStatus.UNAUTHORIZED);

        }

        likeService.deleteLikeByThing(thingId);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @DeleteMapping("/bulletin")
    public ResponseEntity<?> deleteLikeByBulletin(@RequestParam Long bulletinId,
            @RequestHeader("X-User-Id") Long userId) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", userId.toString());

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<BulletinDTO> response = restTemplate.exchange(
                "http://bulletin/api/v1/bulletin/" + bulletinId, HttpMethod.GET, httpEntity,
                BulletinDTO.class);

        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {

            return new ResponseEntity<String>("You Do Not Own This Resource", HttpStatus.UNAUTHORIZED);

        }

        likeService.deleteLikeByBulletin(bulletinId);
        return ResponseEntity.noContent().build();

    }

}
