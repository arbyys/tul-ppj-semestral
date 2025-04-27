package tul.ppj.semestral;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Main application test class that verifies the application context loads correctly.
 * Uses the test profile and disables SQL initialization to avoid issues with schema.sql.
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.sql.init.mode=never",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class TulPpjSemestralApplicationTests {

    /**
     * Verifies that the application context loads successfully
     */
    @Test
    void contextLoads() {
        // This test will fail if the application context cannot be loaded
    }
}
