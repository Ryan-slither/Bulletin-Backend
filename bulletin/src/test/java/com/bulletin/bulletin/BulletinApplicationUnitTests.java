package com.bulletin.bulletin;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bulletin.bulletin.model.BulletinDTO;
import com.bulletin.bulletin.service.BulletinService;

@SpringBootTest(classes = BulletinApplication.class)
class BulletinApplicationUnitTests {

	public class BulletinDTOTestData {

		public static final String TEST_TITLE = "ABC";
		public static final Long TEST_TIME_CREATED = 12L;
		public static final Long TEST_USER_ID = 0L;
		public static final Long TEST_MEMBER_LIMIT = 10L;

		public static final BulletinDTO THING = new BulletinDTO(
				null,
				TEST_TITLE,
				TEST_TIME_CREATED,
				TEST_MEMBER_LIMIT,
				true,
				TEST_USER_ID,
				"");

		public static final String UPDATED_TITLE = "EFG";
		public static final Long UPDATED_MEMBER_LIMIT = 15L;

		public static final BulletinDTO UPDATED_THING = new BulletinDTO(
				null,
				UPDATED_TITLE,
				TEST_TIME_CREATED,
				UPDATED_MEMBER_LIMIT,
				false,
				TEST_USER_ID,
				"");

	}

	@Autowired
	BulletinService bulletinService;

	@Test
	void contextLoads() {
	}

	@Test
	void endToEnd() throws Exception {
		BulletinDTO bulletin = bulletinService.createBulletin(BulletinDTOTestData.THING);
		Long thingId = bulletin.id();

		assertThat(bulletin.title()).isEqualTo(BulletinDTOTestData.TEST_TITLE);
		assertThat(bulletin.memberLimit()).isEqualTo(BulletinDTOTestData.TEST_MEMBER_LIMIT);
		assertThat(bulletin.isOpen()).isTrue();
		assertThat(bulletin.userId()).isEqualTo(BulletinDTOTestData.TEST_USER_ID);

		Optional<BulletinDTO> bulletinById = bulletinService.getBulletinById(thingId);
		assertThat(bulletinById).isNotEmpty();
		assertThat(bulletinById.get().id()).isEqualTo(thingId);

		List<BulletinDTO> bulletinsByUser = bulletinService.getBulletinsByUser(BulletinDTOTestData.TEST_USER_ID);
		assertThat(bulletinsByUser.size()).isGreaterThan(0);
		bulletinsByUser.stream().forEach(bulletinDTO -> {
			assertThat(bulletinDTO.userId()).isEqualTo(BulletinDTOTestData.TEST_USER_ID);
		});

		BulletinDTO updatedThing = bulletinService.updateBulletin(thingId, BulletinDTOTestData.UPDATED_THING);
		assertThat(updatedThing.title()).isEqualTo(BulletinDTOTestData.UPDATED_TITLE);
		assertThat(updatedThing.memberLimit()).isEqualTo(BulletinDTOTestData.UPDATED_MEMBER_LIMIT);
		assertThat(updatedThing.isOpen()).isFalse();

		bulletinService.deleteBulletin(thingId);
		assertThat(bulletinService.getBulletinById(thingId)).isEmpty();
	}

}
