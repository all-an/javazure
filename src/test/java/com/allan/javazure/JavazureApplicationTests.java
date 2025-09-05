package com.allan.javazure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "firebase.enabled=false"
})
class JavazureApplicationTests {

	@Test
	void contextLoads() {
		// This test verifies that the Spring Boot application context loads successfully
		// Firebase is disabled via test properties to avoid requiring credentials in CI
	}

}
