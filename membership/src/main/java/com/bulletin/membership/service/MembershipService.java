package com.bulletin.membership.service;

import java.util.List;
import java.util.Optional;

import com.bulletin.membership.model.MembershipDTO;

public interface MembershipService {

    MembershipDTO createMembership(MembershipDTO membershipDTO);

    Optional<MembershipDTO> getMembershipById(Long id);

    List<MembershipDTO> getAllMembershipByUserId(Long userId);

    void deleteMembership(Long userId, Long bulletinId);

    void deleteMembershipById(Long id);

    void deleteMembershipsByBulletin(Long bulletinId);

    Long countMembershipByBulletin(Long bulletinId);

    Optional<MembershipDTO> getMembershipByBulletinIdAndUserId(Long bulletinId, Long userId);

}
