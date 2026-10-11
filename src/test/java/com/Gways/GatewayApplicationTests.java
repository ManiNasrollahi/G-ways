package com.Gways;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.assertj.core.api.Assertions.assertThat;

class GatewayApplicationTests {

	@Test
	void contextLoads() {
		assertThat(GatewayApplication.class).hasAnnotation(SpringBootApplication.class);
	}

}
