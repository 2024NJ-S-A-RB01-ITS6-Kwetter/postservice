package s_a_rb01_its6.postservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class PostserviceApplicationTests {

	@Test
	void registerUserHappyFlow() {

		assertTrue(true);
	}


}
