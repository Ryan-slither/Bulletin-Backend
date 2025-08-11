package com.bulletin.membership;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bulletin.membership.model.MembershipDTO;
import com.bulletin.membership.service.MembershipService;

@SpringBootTest(classes = MembershipApplication.class)
class MembershipApplicationUnitTests {

	public class MembershipTestDTOData {

		public static final Long TEST_USER_ID1 = 1L;
		public static final Long TEST_BULLETIN_ID1 = 1L;

		public static final MembershipDTO TEST_MEMBERSHIP1 = new MembershipDTO(null, TEST_USER_ID1, TEST_BULLETIN_ID1,
				100L);

		public static final Long TEST_USER_ID2 = 2L;
		public static final Long TEST_BULLETIN_ID2 = 2L;

		public static final MembershipDTO TEST_MEMBERSHIP2 = new MembershipDTO(null, TEST_USER_ID2, TEST_BULLETIN_ID2,
				100L);

		public static final Long TEST_USER_ID3 = 3L;
		public static final Long TEST_BULLETIN_ID3 = 3L;

		public static final MembershipDTO TEST_MEMBERSHIP3 = new MembershipDTO(null, TEST_USER_ID3, TEST_BULLETIN_ID3,
				100L);

		public static final Long TEST_USER_ID4 = 4L;
		public static final Long TEST_BULLETIN_ID4 = 1L;

		public static final MembershipDTO TEST_MEMBERSHIP4 = new MembershipDTO(null, TEST_USER_ID4, TEST_BULLETIN_ID4,
				100L);

	}

	@Autowired
	MembershipService membershipService;

	@Test
	void contextLoads() {
	}

	@Test
	void endToEnd() {
		MembershipDTO membership1 = membershipService.createMembership(MembershipTestDTOData.TEST_MEMBERSHIP1);
		MembershipDTO membership2 = membershipService.createMembership(MembershipTestDTOData.TEST_MEMBERSHIP2);
		MembershipDTO membership3 = membershipService.createMembership(MembershipTestDTOData.TEST_MEMBERSHIP3);
		membershipService.createMembership(MembershipTestDTOData.TEST_MEMBERSHIP4);

		Optional<MembershipDTO> m1ById = membershipService.getMembershipById(membership1.id());
		assertThat(m1ById).isNotEmpty();
		assertThat(m1ById.get().bulletinId()).isEqualTo(MembershipTestDTOData.TEST_BULLETIN_ID1);
		assertThat(m1ById.get().userId()).isEqualTo(MembershipTestDTOData.TEST_USER_ID1);

		assertThat(membershipService.countMembershipByBulletin(MembershipTestDTOData.TEST_BULLETIN_ID1)).isEqualTo(2L);

		membershipService.deleteMembershipById(membership3.id());
		assertThat(membershipService.getMembershipById(membership3.id())).isEmpty();

		membershipService.deleteMembership(membership2.userId(), membership2.bulletinId());
		assertThat(membershipService.getMembershipById(membership2.id())).isEmpty();

		membershipService.deleteMembershipsByBulletin(MembershipTestDTOData.TEST_BULLETIN_ID1);
		assertThat(membershipService.countMembershipByBulletin(MembershipTestDTOData.TEST_BULLETIN_ID1)).isEqualTo(0L);
	}

}
