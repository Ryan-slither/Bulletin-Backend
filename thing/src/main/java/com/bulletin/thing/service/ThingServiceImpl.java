package com.bulletin.thing.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bulletin.thing.model.Thing;
import com.bulletin.thing.model.ThingDTO;
import com.bulletin.thing.repository.ThingRepository;

@Service
public class ThingServiceImpl implements ThingService {

    private final ThingRepository thingRepository;

    private final RestTemplate restTemplate;

    @Value("${TARGET}")
    private String target;

    public ThingServiceImpl(ThingRepository thingRepository, RestTemplate restTemplate) {
        this.thingRepository = thingRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public ThingDTO createThing(ThingDTO thingDTO) {
        Thing thing = convertToEntity(thingDTO);
        Thing savedThing = thingRepository.save(thing);
        return convertToDTO(savedThing);
    }

    @Override
    public Optional<ThingDTO> getThingById(Long id) {
        return thingRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    public ThingDTO updateThing(Long id, ThingDTO thingDTO) {
        Thing thing = thingRepository.findById(id).orElseThrow();
        if (thingDTO.content() != null) {
            thing.setContent(thingDTO.content());
            Thing updatedThing = thingRepository.save(thing);
            return convertToDTO(updatedThing);
        }
        throw new IllegalStateException("Cannot Update With Empty Content");
    }

    @Override
    public List<ThingDTO> getThingsByBulletin(Long bulletinId) {
        return thingRepository.findByBulletinId(bulletinId).stream().map(this::convertToDTO).toList();
    }

    @Override
    public void deleteThing(Long id, Long userId) {

        if (!target.equals("unittest")) {

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Id", userId.toString());

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            // Returns String For Error & Void For Success
            ResponseEntity<?> response = restTemplate
                    .exchange("http://userlike/api/v1/userlike/thing?thingId=" + id, HttpMethod.DELETE,
                            httpEntity, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {

                throw new IllegalStateException("Cannot Delete User Likes Associated With Thing");

            }

        }

        thingRepository.deleteById(id);

    }

    @Override
    public void deleteThingsByBulletin(Long bulletinId, Long userId) {

        if (!target.equals("unittest")) {

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Id", userId.toString());

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            // Returns String For Error & Void For Success
            ResponseEntity<?> response = restTemplate
                    .exchange("http://userlike/api/v1/userlike/bulletin?bulletinId=" + bulletinId, HttpMethod.DELETE,
                            httpEntity, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {

                throw new IllegalStateException("Cannot Delete Like Associated With Bulletin");

            }

        }

        thingRepository.deleteByBulletinId(bulletinId);

    }

    private Thing convertToEntity(ThingDTO thingDTO) {
        Thing thing = new Thing();
        thing.setBulletinId(thingDTO.bulletinId());
        thing.setContent(thingDTO.content());
        thing.setTimeCreated(Instant.now().getEpochSecond());
        thing.setUserId(thingDTO.userId());
        return thing;
    }

    private ThingDTO convertToDTO(Thing thing) {
        return new ThingDTO(thing.getId(), thing.getContent(), thing.getBulletinId(), thing.getUserId(),
                thing.getTimeCreated());
    }

}
