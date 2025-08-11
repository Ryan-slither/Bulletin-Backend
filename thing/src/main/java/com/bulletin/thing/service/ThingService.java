package com.bulletin.thing.service;

import java.util.List;
import java.util.Optional;

import com.bulletin.thing.model.ThingDTO;

public interface ThingService {

    ThingDTO createThing(ThingDTO thingDTO);

    Optional<ThingDTO> getThingById(Long id);

    List<ThingDTO> getThingsByBulletin(Long bulletinId);

    ThingDTO updateThing(Long id, ThingDTO thingDTO);

    void deleteThingsByBulletin(Long bulletinId, Long userId);

    void deleteThing(Long id, Long userId);

}
