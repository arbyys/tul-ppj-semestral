package tul.ppj.semestral.config;

import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import tul.ppj.semestral.service.WeatherApiService;

import javax.sql.DataSource;

/**
 * Test configuration class that provides beans for the test environment.
 * This ensures that tests use the in-memory H2 database and mocked services where needed.
 */
@TestConfiguration
@Profile("test")
public class TestConfig {

    /**
     * Creates an in-memory H2 database for testing
     * @return DataSource configured for H2
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("testdb")
                .build();
    }

    /**
     * Override the default SQL initializer to use our test-specific schema
     */
    @Bean
    @Primary
    public SqlDataSourceScriptDatabaseInitializer dataSourceScriptDatabaseInitializer(
            DataSource dataSource, SqlInitializationProperties properties) {

        // Create a custom database initializer that uses our test schema
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("schema-test.sql"));
        populator.addScript(new ClassPathResource("data-test.sql"));
        populator.execute(dataSource);

        // Return a no-op initializer to prevent the default one from running
        return new SqlDataSourceScriptDatabaseInitializer(dataSource, properties) {
            @Override
            public boolean initializeDatabase() {
                // Skip the default initialization
                return true;
            }
        };
    }

    /**
     * Creates a mock of the WeatherApiService to avoid external API calls during tests
     * @return Mocked WeatherApiService
     */
    @Bean
    @Primary
    public WeatherApiService weatherApiService() {
        return Mockito.mock(WeatherApiService.class);
    }
}
