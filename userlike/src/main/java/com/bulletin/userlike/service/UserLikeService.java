package com.bulletin.userlike.service;

import java.util.List;
import java.util.Optional;

import com.bulletin.userlike.model.UserLikeCountDTO;
import com.bulletin.userlike.model.UserLikeDTO;

public interface UserLikeService {

    UserLikeDTO createLike(UserLikeDTO likeDTO);

    Optional<UserLikeDTO> getLikeById(Long id);

    Long getLikeCountByThing(Long thingId);

    List<UserLikeCountDTO> getLikeCountByBulletin(Long bulletinId);

    List<UserLikeDTO> getLikesByUserAndBulletin(Long userId, Long bulletinId);

    Optional<UserLikeDTO> getLikeByUserAndThing(Long userId, Long thingId);

    void deleteLikeByUserAndThing(Long userId, Long thingId);

    void deleteLikeByThing(Long thingId);

    void deleteLikeByBulletin(Long bulletinId);

    void deleteLikeById(Long id);

}
