package com.bulletin.thing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bulletin.thing.model.ThingDTO;
import com.bulletin.thing.service.ThingService;

@SpringBootTest(classes = ThingApplication.class)
class ThingApplicationUnitTests {

	public class ThingDTOTestData {

		public static final String TEST_CONTENT = "ABC";
		public static final Long TEST_BULLETIN_ID = 12L;
		public static final Long TEST_USER_ID = 0L;

		public static final ThingDTO THING = new ThingDTO(
				null,
				TEST_CONTENT,
				TEST_BULLETIN_ID,
				TEST_USER_ID,
				null);

		public static final String UPDATED_CONTENT = "EFG";

		public static final ThingDTO UPDATED_THING = new ThingDTO(
				null,
				UPDATED_CONTENT,
				TEST_BULLETIN_ID,
				TEST_USER_ID,
				null);

	}

	@Autowired
	ThingService thingService;

	@Test
	void contextLoads() {
	}

	@Test
	void endToEnd() throws Exception {
		ThingDTO thing = thingService.createThing(ThingDTOTestData.THING);
		Long thingId = thing.id();

		assertThat(thing.content()).isEqualTo(ThingDTOTestData.TEST_CONTENT);
		assertThat(thing.bulletinId()).isEqualTo(ThingDTOTestData.TEST_BULLETIN_ID);
		assertThat(thing.userId()).isEqualTo(ThingDTOTestData.TEST_USER_ID);
		assertThat(thing.timeCreated()).isNotNull();

		Optional<ThingDTO> thingById = thingService.getThingById(thingId);
		assertThat(thingById).isNotEmpty();
		assertThat(thingById.get().id()).isEqualTo(thingId);

		List<ThingDTO> thingsByBulletin = thingService.getThingsByBulletin(ThingDTOTestData.TEST_BULLETIN_ID);
		assertThat(thingsByBulletin.size()).isGreaterThan(0);
		thingsByBulletin.stream().forEach(thingDTO -> {
			assertThat(thingDTO.bulletinId()).isEqualTo(ThingDTOTestData.TEST_BULLETIN_ID);
		});

		ThingDTO updatedThing = thingService.updateThing(thingId, ThingDTOTestData.UPDATED_THING);
		assertThat(updatedThing.content()).isEqualTo(ThingDTOTestData.UPDATED_CONTENT);

		thingService.deleteThing(thingId, null);
		assertThat(thingService.getThingById(thingId)).isEmpty();
	}

}
