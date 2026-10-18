package ru.itlearn.MyUiRestDbService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties="spring.datasource.url=jdbc:h2:mem:smoke7;DB_CLOSE_DELAY=-1")
class MyUiRestDbServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
