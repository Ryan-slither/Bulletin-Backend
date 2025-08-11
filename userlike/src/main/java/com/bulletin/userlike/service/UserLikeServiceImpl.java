package com.bulletin.userlike.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bulletin.userlike.model.UserLike;
import com.bulletin.userlike.model.UserLikeCountDTO;
import com.bulletin.userlike.model.UserLikeDTO;
import com.bulletin.userlike.repository.UserLikeRepository;

@Service
public class UserLikeServiceImpl implements UserLikeService {

    private final UserLikeRepository likeRepository;

    public UserLikeServiceImpl(UserLikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @Override
    public UserLikeDTO createLike(UserLikeDTO likeDTO) {

        Optional<UserLikeDTO> userLike = getLikeByUserAndThing(likeDTO.userId(), likeDTO.thingId());

        if (userLike.isEmpty()) {

            UserLike newUserLike = convertToEntity(likeDTO);
            UserLike savedLike = likeRepository.save(newUserLike);
            return convertToDTO(savedLike);

        } else {

            return userLike.get();

        }

    }

    @Override
    public Optional<UserLikeDTO> getLikeById(Long id) {

        return likeRepository.findById(id).map(this::convertToDTO);

    }

    @Override
    public Long getLikeCountByThing(Long thingId) {

        return likeRepository.countByThingId(thingId);

    }

    @Override
    public List<UserLikeCountDTO> getLikeCountByBulletin(Long bulletinId) {

        return likeRepository.countByBulletin(bulletinId);

    }

    @Override
    public List<UserLikeDTO> getLikesByUserAndBulletin(Long userId, Long bulletinId) {

        return likeRepository.findByUserIdAndBulletinId(userId, bulletinId).stream().map(this::convertToDTO).toList();

    }

    @Override
    public Optional<UserLikeDTO> getLikeByUserAndThing(Long userId, Long thingId) {

        return likeRepository.findByUserIdAndThingId(userId, thingId).map(this::convertToDTO);

    }

    @Override
    public void deleteLikeByUserAndThing(Long userId, Long thingId) {

        likeRepository.deleteByUserIdAndThingId(thingId, thingId);

    }

    @Override
    public void deleteLikeByThing(Long thingId) {

        likeRepository.deleteByThingId(thingId);

    }

    @Override
    public void deleteLikeByBulletin(Long bulletinId) {

        likeRepository.deleteByBulletinId(bulletinId);

    }

    @Override
    public void deleteLikeById(Long id) {

        likeRepository.deleteById(id);

    }

    private UserLike convertToEntity(UserLikeDTO likeDTO) {

        UserLike like = new UserLike();
        like.setThingId(likeDTO.thingId());
        like.setUserId(likeDTO.userId());
        like.setBulletinId(likeDTO.bulletinId());
        return like;

    }

    private UserLikeDTO convertToDTO(UserLike like) {

        return new UserLikeDTO(like.getId(), like.getThingId(), like.getUserId(), like.getBulletinId());

    }

}
