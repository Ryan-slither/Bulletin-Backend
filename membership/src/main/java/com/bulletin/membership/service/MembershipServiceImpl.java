package com.bulletin.membership.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bulletin.membership.model.Membership;
import com.bulletin.membership.model.MembershipDTO;
import com.bulletin.membership.repository.MembershipRepository;

@Service
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;

    public MembershipServiceImpl(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    @Override
    public MembershipDTO createMembership(MembershipDTO membershipDTO) {

        Optional<Membership> existingMembership = membershipRepository.findByUserIdAndBulletinId(membershipDTO.userId(),
                membershipDTO.bulletinId());
        Long membershipCount = countMembershipByBulletin(membershipDTO.bulletinId());

        if (membershipCount >= membershipDTO.memberLimit()) {

            throw new IllegalStateException("Cannot Have Duplicate Memberships OR Membership Limit Reached");

        }

        if (!existingMembership.isEmpty()) {

            return convertToDTO(existingMembership.get());

        }

        Membership membership = convertToEntity(membershipDTO);
        Membership savedMembership = membershipRepository.save(membership);
        return convertToDTO(savedMembership);

    }

    @Override
    public Optional<MembershipDTO> getMembershipById(Long id) {

        return membershipRepository.findById(id).map(this::convertToDTO);

    }

    @Override
    public List<MembershipDTO> getAllMembershipByUserId(Long userId) {

        return membershipRepository.findByUserId(userId).stream().map(this::convertToDTO).toList();

    }

    @Override
    public void deleteMembershipById(Long id) {

        membershipRepository.deleteById(id);

    }

    @Override
    public void deleteMembership(Long userId, Long bulletinId) {

        membershipRepository.deleteByUserIdAndBulletinId(userId, bulletinId);

    }

    @Override
    public void deleteMembershipsByBulletin(Long bulletinId) {

        membershipRepository.deleteByBulletinId(bulletinId);

    }

    @Override
    public Long countMembershipByBulletin(Long bulletinId) {

        return membershipRepository.countByBulletinId(bulletinId);

    }

    @Override
    public Optional<MembershipDTO> getMembershipByBulletinIdAndUserId(Long bulletinId, Long userId) {

        return membershipRepository.findByUserIdAndBulletinId(userId, bulletinId).map(this::convertToDTO);

    }

    private Membership convertToEntity(MembershipDTO membershipDTO) {
        Membership membership = new Membership();
        membership.setBulletinId(membershipDTO.bulletinId());
        membership.setUserId(membershipDTO.userId());
        return membership;
    }

    private MembershipDTO convertToDTO(Membership membership) {
        return new MembershipDTO(membership.getId(), membership.getUserId(), membership.getBulletinId(), null);
    }

}
