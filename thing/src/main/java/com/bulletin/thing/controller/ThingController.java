package com.bulletin.thing.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.bulletin.thing.model.BulletinDTO;
import com.bulletin.thing.model.MembershipDTO;
import com.bulletin.thing.model.ThingDTO;
import com.bulletin.thing.service.ThingService;

@RestController
@RequestMapping("/api/v1/thing")
public class ThingController {

    private final ThingService thingService;

    private final RestTemplate restTemplate;

    public ThingController(ThingService thingService, RestTemplate restTemplate) {
        this.thingService = thingService;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getThingById(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {

        Optional<ThingDTO> thing = thingService.getThingById(id);

        if (thing.isEmpty() || !thing.get().userId().equals(userId)) {

            return new ResponseEntity<>("You Cannot Access A Resource You Do Not Own", HttpStatus.UNAUTHORIZED);

        }

        return thing.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @GetMapping("/things")
    public ResponseEntity<?> getThingsByBulletin(@RequestParam Long bulletinId,
            @RequestHeader("X-User-Id") Long userId) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-id", userId.toString());

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<MembershipDTO> response = restTemplate
                .exchange("http://membership/api/v1/membership/" + bulletinId + "/" + userId, HttpMethod.GET,
                        httpEntity, MembershipDTO.class);

        if (response.getStatusCode() != HttpStatus.OK) {

            return new ResponseEntity<String>("Not Allowed To Access This Bulletin", HttpStatus.UNAUTHORIZED);
            
        }

        return new ResponseEntity<List<ThingDTO>>(thingService.getThingsByBulletin(bulletinId), HttpStatus.OK);

    }

    @DeleteMapping("/bulletin")
    public ResponseEntity<?> deleteThingsByBulletin(@RequestParam Long bulletinId,
            @RequestHeader("X-User-Id") Long userId) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", userId.toString());

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<BulletinDTO> response = restTemplate.exchange(
                "http://bulletin/api/v1/bulletin/" + bulletinId, HttpMethod.GET, httpEntity,
                BulletinDTO.class);

        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {

            return new ResponseEntity<String>("You Are Not Part Of This Bulletin", HttpStatus.UNAUTHORIZED);

        }

        BulletinDTO bulletin = response.getBody();

        if (bulletin == null || bulletin.userId() != userId) {

            return new ResponseEntity<String>("Unauthorized Access", HttpStatus.UNAUTHORIZED);

        }

        thingService.deleteThingsByBulletin(bulletinId, userId);
        return ResponseEntity.noContent().build();

    }
}
