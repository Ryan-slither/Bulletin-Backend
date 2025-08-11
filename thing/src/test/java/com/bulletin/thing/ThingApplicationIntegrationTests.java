package com.bulletin.thing;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ThingApplication.class)
public class ThingApplicationIntegrationTests {

    @BeforeAll
	static void waitForDependencies() throws Exception {
		int tries = 0;

		while (tries < 5) {
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL("http://bulletin:8082/actuator/health")
						.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(5000);
				conn.connect();

				if (conn.getResponseCode() == 200) {
					return;
				}
			} catch (Exception e) {
			}

			tries++;
			Thread.sleep(1000);
		}

        throw new IllegalStateException("Connection to Bulletin Service Not Established");
	}

    @Test
    void contextLoads() {
    }

}
