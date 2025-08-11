package com.bulletin.userlike;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bulletin.userlike.model.UserLikeDTO;
import com.bulletin.userlike.service.UserLikeService;

@SpringBootTest(classes = UserLikeApplication.class)
class UserLikeApplicationUnitTests {

	public class LikeDTOTestData {
		public static final Long TEST_THING_ID1 = 1L;
		public static final Long TEST_USER_ID1 = 1L;
		public static final Long TEST_BULLETIN_ID1 = 1L;

		public static final UserLikeDTO LIKE1 = new UserLikeDTO(null, TEST_THING_ID1, TEST_USER_ID1, TEST_BULLETIN_ID1);

		public static final Long TEST_THING_ID2 = 1L;
		public static final Long TEST_USER_ID2 = 2L;
		public static final Long TEST_BULLETIN_ID2 = 1L;

		public static final UserLikeDTO LIKE2 = new UserLikeDTO(null, TEST_THING_ID2, TEST_USER_ID2, TEST_BULLETIN_ID2);

		public static final Long TEST_THING_ID3 = 3L;
		public static final Long TEST_USER_ID3 = 3L;
		public static final Long TEST_BULLETIN_ID3 = 1L;

		public static final UserLikeDTO LIKE3 = new UserLikeDTO(null, TEST_THING_ID3, TEST_USER_ID3, TEST_BULLETIN_ID3);

		public static final Long TEST_THING_ID4 = 2L;
		public static final Long TEST_USER_ID4 = 2L;
		public static final Long TEST_BULLETIN_ID4 = 1L;

		public static final UserLikeDTO LIKE4 = new UserLikeDTO(null, TEST_THING_ID4, TEST_USER_ID4, TEST_BULLETIN_ID4);
	}

	@Autowired
	UserLikeService likeService;

	@Test
	void contextLoads() {
	}

	@Test
	void endToEnd() throws Exception {
		UserLikeDTO like1 = likeService.createLike(LikeDTOTestData.LIKE1);
		UserLikeDTO like2 = likeService.createLike(LikeDTOTestData.LIKE2);
		UserLikeDTO like3 = likeService.createLike(LikeDTOTestData.LIKE3);
		UserLikeDTO like4 = likeService.createLike(LikeDTOTestData.LIKE4);

		assertThat(like1.bulletinId()).isEqualTo(LikeDTOTestData.TEST_BULLETIN_ID1);
		assertThat(like1.thingId()).isEqualTo(LikeDTOTestData.TEST_THING_ID1);
		assertThat(like1.userId()).isEqualTo(LikeDTOTestData.TEST_USER_ID1);

		Optional<UserLikeDTO> likeById = likeService.getLikeById(like1.id());
		assertThat(likeById).isNotEmpty();

		Long likeCount = likeService.getLikeCountByThing(1L);
		assertThat(likeCount).isGreaterThan(1L);

		List<UserLikeDTO> likeList = likeService.getLikesByUserAndBulletin(LikeDTOTestData.TEST_USER_ID1,
				LikeDTOTestData.TEST_BULLETIN_ID1);
		assertThat(likeList.size()).isEqualTo(1);

		likeService.deleteLikeByUserAndThing(3L, 3L);
		assertThat(likeService.getLikeById(like3.id())).isEmpty();

		likeService.deleteLikeByThing(2L);
		assertThat(likeService.getLikeById(like4.id())).isEmpty();

		likeService.deleteLikeByBulletin(1L);
		assertThat(likeService.getLikeById(like1.id())).isEmpty();
		assertThat(likeService.getLikeById(like2.id())).isEmpty();
	}

}
