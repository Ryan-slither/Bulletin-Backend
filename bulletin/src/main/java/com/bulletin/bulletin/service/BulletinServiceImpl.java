package com.bulletin.bulletin.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bulletin.bulletin.model.Bulletin;
import com.bulletin.bulletin.model.BulletinDTO;
import com.bulletin.bulletin.model.BulletinJoinDTO;
import com.bulletin.bulletin.model.BulletinJoinResponseDTO;
import com.bulletin.bulletin.model.MembershipDTO;
import com.bulletin.bulletin.repository.BulletinRepository;

@Service
public class BulletinServiceImpl implements BulletinService {

    private final BulletinRepository bulletinRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${TARGET}")
    private String target;

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public BulletinServiceImpl(BulletinRepository bulletinRepository) {
        this.bulletinRepository = bulletinRepository;
    }

    @Override
    public BulletinDTO createBulletin(BulletinDTO bulletinDTO) {

        if (bulletinDTO.title().isEmpty()) {

            throw new IllegalStateException("Cannot Create Bulletin Without A Name");

        }

        Bulletin bulletin = convertToEntity(bulletinDTO);
        bulletin.setJoinCode(generateJoinCode());
        bulletin.setIsOpen(true);
        Bulletin savedBulletin = bulletinRepository.save(bulletin);

        if (joinBulletin(bulletin.getJoinCode(), bulletin.getUserId()) == -1L) {

            throw new IllegalStateException("User Could Not Join Their Own Bulletin");

        }

        return convertToDTO(savedBulletin);

    }

    @Override
    public Optional<BulletinDTO> getBulletinById(Long id) {

        return bulletinRepository.findById(id).map(this::convertToDTO);

    }

    @Override
    public BulletinDTO updateBulletin(Long id, BulletinDTO bulletinDTO) {

        Bulletin bulletin = bulletinRepository.findById(id).orElseThrow();

        if (bulletinDTO.memberLimit() != null) {

            bulletin.setMemberLimit(bulletinDTO.memberLimit());

        }

        if (bulletinDTO.isOpen() != null) {

            bulletin.setIsOpen(bulletinDTO.isOpen());

            if (!bulletinDTO.isOpen()) {

                bulletin.setJoinCode("");

            }

        }

        Bulletin updatedBulletin = bulletinRepository.save(bulletin);
        return convertToDTO(updatedBulletin);

    }

    @Override
    public List<BulletinDTO> getBulletinsByUser(Long userId) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", userId.toString());
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        final String membershipUri = "http://membership/api/v1/membership/all/" + userId;

        ResponseEntity<List<MembershipDTO>> response = restTemplate.exchange(membershipUri,
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<List<MembershipDTO>>() {
                });

        List<MembershipDTO> memberships = response.getBody();

        if (memberships == null) {
            return List.of();
        }

        List<Long> bulletinIds = memberships.stream().map(MembershipDTO::bulletinId).toList();

        return bulletinRepository.findAllById(bulletinIds).stream()
                .map(this::convertToDTO).toList();

    }

    @Override
    public void deleteBulletin(Long id) {

        bulletinRepository.deleteById(id);

        if (!target.equals("unittest")) {

            final String thingUri = "http://thing/api/v1/thing/bulletin?bulletinId=" + id;
            restTemplate.delete(thingUri);

        }

    }

    @Override
    public Long joinBulletin(String joinCode, Long userId) {

        Optional<Bulletin> bulletinToJoin = bulletinRepository.findByJoinCode(joinCode);

        if (!bulletinToJoin.isEmpty() && bulletinToJoin.get().getIsOpen()) {

            BulletinJoinDTO bulletinJoinDTO = new BulletinJoinDTO(null, userId, bulletinToJoin.get().getId(),
                    bulletinToJoin.get().getMemberLimit());

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Id", userId.toString());
            HttpEntity<BulletinJoinDTO> httpEntity = new HttpEntity<>(bulletinJoinDTO, headers);

            final String membershipUri = "http://membership/api/v1/membership";

            ResponseEntity<BulletinJoinResponseDTO> response = restTemplate.exchange(membershipUri,
                    HttpMethod.POST,
                    httpEntity,
                    BulletinJoinResponseDTO.class);

            BulletinJoinResponseDTO membership = response.getBody();
            if (membership != null && membership.userId().equals(userId)) {

                return membership.bulletinId();

            }

        }

        return -1L;

    }

    private String generateJoinCode() {
        Long i = 0L;
        while (i < 1000) {
            StringBuilder sb = new StringBuilder(6);

            for (int j = 0; j < 6; j++) {
                int idx = RANDOM.nextInt(ALPHABET.length());
                sb.append(ALPHABET.charAt(idx));
            }

            String joinString = sb.toString();
            Optional<Bulletin> bulletin = bulletinRepository.findByJoinCode(sb.toString());

            if (!bulletin.isEmpty()) {
                continue;
            }

            return joinString;
        }

        throw new IllegalStateException("Valid Code Could Not Be Generated");
    }

    private Bulletin convertToEntity(BulletinDTO bulletinDTO) {
        Bulletin bulletin = new Bulletin();
        bulletin.setUserId(bulletinDTO.userId());
        bulletin.setTitle(bulletinDTO.title());
        bulletin.setTimeCreated(Instant.now().getEpochSecond());
        bulletin.setIsOpen(bulletinDTO.isOpen());
        bulletin.setMemberLimit(bulletinDTO.memberLimit());
        bulletin.setJoinCode(bulletinDTO.joinCode());
        return bulletin;
    }

    private BulletinDTO convertToDTO(Bulletin bulletin) {
        return new BulletinDTO(bulletin.getId(), bulletin.getTitle(), bulletin.getTimeCreated(),
                bulletin.getMemberLimit(), bulletin.getIsOpen(), bulletin.getUserId(), bulletin.getJoinCode());
    }

}
