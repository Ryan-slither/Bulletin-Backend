package com.bulletin.thing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bulletin.thing.model.Thing;

public interface ThingRepository extends JpaRepository<Thing, Long> {
    List<Thing> findByBulletinId(Long bulletinId);

    void deleteByBulletinId(Long bulletinId);
}
