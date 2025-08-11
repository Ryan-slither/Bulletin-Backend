package com.bulletin.membership.controller;

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

import com.bulletin.membership.model.BulletinDTO;
import com.bulletin.membership.model.MembershipDTO;
import com.bulletin.membership.service.MembershipService;

@RestController
@RequestMapping("/api/v1/membership")
public class MembershipController {

    private final MembershipService membershipService;

    private final RestTemplate restTemplate;

    public MembershipController(MembershipService membershipService, RestTemplate restTemplate) {
        this.membershipService = membershipService;
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public ResponseEntity<?> createMembership(@RequestBody MembershipDTO membershipDTO,
            @RequestHeader("X-User-Id") Long userId) {

        if (userId != membershipDTO.userId()) {

            return new ResponseEntity<String>("Creating Membership For User Not Your Own", HttpStatus.UNAUTHORIZED);

        }

        return new ResponseEntity<MembershipDTO>(membershipService.createMembership(membershipDTO), HttpStatus.CREATED);

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMembershipById(@PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {

        Optional<MembershipDTO> membership = membershipService.getMembershipById(id);

        if (membership.isEmpty() || userId != membership.get().userId()) {
            return new ResponseEntity<String>("Cannot Access This Membership", HttpStatus.UNAUTHORIZED);
        }

        return membership.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<?> getAllMembershipByUserId(
            @PathVariable Long userId, @RequestHeader("X-User-Id") Long headerUserId) {

        if (userId != headerUserId) {
            return new ResponseEntity<String>("Not Allowed To Access This Membership", HttpStatus.UNAUTHORIZED);
        }

        List<MembershipDTO> memberships = membershipService.getAllMembershipByUserId(userId);

        if (memberships.isEmpty()) {
            return new ResponseEntity<String>("Memberships Not Found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<List<MembershipDTO>>(memberships, HttpStatus.OK);

    }

    @GetMapping("/{bulletinId}/{userId}")
    public ResponseEntity<?> getMembershipByUserIdAndBulletinId(@PathVariable Long bulletinId,
            @PathVariable Long userId, @RequestHeader("X-User-Id") Long headerUserId) {

        if (userId != headerUserId) {

            return new ResponseEntity<String>("Not Allowed To Access This Membership", HttpStatus.UNAUTHORIZED);
            
        }

        Optional<MembershipDTO> membership = membershipService.getMembershipByBulletinIdAndUserId(bulletinId, userId);

        if (membership.isEmpty()) {

            return new ResponseEntity<String>("Membership Not Found", HttpStatus.NOT_FOUND);

        }

        return membership.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @GetMapping
    public ResponseEntity<?> getMembershipCount(@RequestParam Long bulletinId,
            @RequestHeader("X-User-Id") Long userId) {

        Optional<MembershipDTO> membership = membershipService.getMembershipByBulletinIdAndUserId(bulletinId, userId);

        if (membership.isEmpty()) {
            return new ResponseEntity<String>("You Are Not A Member Of This Bulletin", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<Long>(membershipService.countMembershipByBulletin(bulletinId), HttpStatus.OK);

    }

    @DeleteMapping
    public ResponseEntity<?> deleteMembership(@RequestBody MembershipDTO membershipDTO,
            @RequestHeader("X-User-Id") Long userId) {

        if (membershipDTO.id() != null) {

            Optional<MembershipDTO> membership = membershipService.getMembershipById(membershipDTO.id());

            if (membership.isEmpty() || membership.get().userId() != userId) {
                return new ResponseEntity<String>("Cannot Access This Membership", HttpStatus.UNAUTHORIZED);
            }

            membershipService.deleteMembershipById(membershipDTO.id());

        } else {

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Id", userId.toString());

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            ResponseEntity<BulletinDTO> response = restTemplate
                    .exchange("http://bulletin/api/v1/bulletin/" + membershipDTO.bulletinId(), HttpMethod.GET,
                            httpEntity,
                            BulletinDTO.class);

            BulletinDTO bulletin = response.getBody();

            if (bulletin == null || bulletin.userId() != userId) {
                return new ResponseEntity<String>("Cannot Access This Membership", HttpStatus.UNAUTHORIZED);
            }

            membershipService.deleteMembership(membershipDTO.userId(), membershipDTO.bulletinId());

        }

        return ResponseEntity.noContent().build();

    }

}
