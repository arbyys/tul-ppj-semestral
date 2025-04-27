package tul.ppj.semestral.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import tul.ppj.semestral.dto.CityDTO;
import tul.ppj.semestral.dto.CountryDTO;
import tul.ppj.semestral.dto.RecordDTO;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.sql.init.mode=never",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class ApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCountryEndpoints() {
        String baseUrl = "http://localhost:" + port + "/api/countries";
        
        // Create a country
        CountryDTO countryToCreate = new CountryDTO(null, "Test Country", "TST", 1000000L, "Test Continent");
        ResponseEntity<CountryDTO> createResponse = restTemplate.postForEntity(baseUrl, countryToCreate, CountryDTO.class);
        
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getId());
        assertEquals("Test Country", createResponse.getBody().getName());
        
        Long countryId = createResponse.getBody().getId();
        
        // Get the country by ID
        ResponseEntity<CountryDTO> getResponse = restTemplate.getForEntity(baseUrl + "/" + countryId, CountryDTO.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(countryId, getResponse.getBody().getId());
        assertEquals("Test Country", getResponse.getBody().getName());
        
        // Update the country
        CountryDTO updatedCountry = new CountryDTO(countryId, "Updated Country", "UPD", 1100000L, "Test Continent");
        ResponseEntity<CountryDTO> updateResponse = restTemplate.exchange(
                baseUrl + "/" + countryId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedCountry),
                CountryDTO.class);
        
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals("Updated Country", updateResponse.getBody().getName());
        
        // Get all countries
        ResponseEntity<CountryDTO[]> getAllResponse = restTemplate.getForEntity(baseUrl, CountryDTO[].class);
        assertEquals(HttpStatus.OK, getAllResponse.getStatusCode());
        assertTrue(getAllResponse.getBody().length > 0);
        
        // Delete the country
        restTemplate.delete(baseUrl + "/" + countryId);
        
        // Verify deletion
        ResponseEntity<CountryDTO> getDeletedResponse = restTemplate.getForEntity(baseUrl + "/" + countryId, CountryDTO.class);
        assertEquals(HttpStatus.NOT_FOUND, getDeletedResponse.getStatusCode());
    }

    @Test
    public void testCityEndpoints() {
        String countryUrl = "http://localhost:" + port + "/api/countries";
        String cityUrl = "http://localhost:" + port + "/api/cities";
        
        // Create a country first
        CountryDTO countryToCreate = new CountryDTO(null, "Test Country", "TST", 1000000L, "Test Continent");
        ResponseEntity<CountryDTO> countryResponse = restTemplate.postForEntity(countryUrl, countryToCreate, CountryDTO.class);
        
        assertEquals(HttpStatus.CREATED, countryResponse.getStatusCode());
        Long countryId = countryResponse.getBody().getId();
        
        // Create a city
        CityDTO cityToCreate = new CityDTO(null, "Test City", countryId, null, 500000L, 50.0, 15.0);
        ResponseEntity<CityDTO> createResponse = restTemplate.postForEntity(cityUrl, cityToCreate, CityDTO.class);
        
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getId());
        assertEquals("Test City", createResponse.getBody().getName());
        assertEquals(countryId, createResponse.getBody().getCountryId());
        
        Long cityId = createResponse.getBody().getId();
        
        // Get the city by ID
        ResponseEntity<CityDTO> getResponse = restTemplate.getForEntity(cityUrl + "/" + cityId, CityDTO.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(cityId, getResponse.getBody().getId());
        assertEquals("Test City", getResponse.getBody().getName());
        
        // Get cities by country ID
        ResponseEntity<CityDTO[]> getByCountryResponse = restTemplate.getForEntity(cityUrl + "/country/" + countryId, CityDTO[].class);
        assertEquals(HttpStatus.OK, getByCountryResponse.getStatusCode());
        assertTrue(getByCountryResponse.getBody().length > 0);
        assertEquals("Test City", getByCountryResponse.getBody()[0].getName());
        
        // Update the city
        CityDTO updatedCity = new CityDTO(cityId, "Updated City", countryId, null, 550000L, 50.1, 15.1);
        ResponseEntity<CityDTO> updateResponse = restTemplate.exchange(
                cityUrl + "/" + cityId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedCity),
                CityDTO.class);
        
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals("Updated City", updateResponse.getBody().getName());
        
        // Delete the city
        restTemplate.delete(cityUrl + "/" + cityId);
        
        // Verify deletion
        ResponseEntity<CityDTO> getDeletedResponse = restTemplate.getForEntity(cityUrl + "/" + cityId, CityDTO.class);
        assertEquals(HttpStatus.NOT_FOUND, getDeletedResponse.getStatusCode());
        
        // Clean up - delete the country
        restTemplate.delete(countryUrl + "/" + countryId);
    }

    @Test
    public void testRecordEndpoints() {
        String countryUrl = "http://localhost:" + port + "/api/countries";
        String cityUrl = "http://localhost:" + port + "/api/cities";
        String recordUrl = "http://localhost:" + port + "/api/records";
        
        // Create a country first
        CountryDTO countryToCreate = new CountryDTO(null, "Test Country", "TST", 1000000L, "Test Continent");
        ResponseEntity<CountryDTO> countryResponse = restTemplate.postForEntity(countryUrl, countryToCreate, CountryDTO.class);
        Long countryId = countryResponse.getBody().getId();
        
        // Create a city
        CityDTO cityToCreate = new CityDTO(null, "Test City", countryId, null, 500000L, 50.0, 15.0);
        ResponseEntity<CityDTO> cityResponse = restTemplate.postForEntity(cityUrl, cityToCreate, CityDTO.class);
        Long cityId = cityResponse.getBody().getId();
        
        // Create a record
        RecordDTO recordToCreate = new RecordDTO();
        recordToCreate.setMinTemperature(10.5);
        recordToCreate.setMaxTemperature(22.3);
        recordToCreate.setPressure(1013);
        recordToCreate.setHumidity(65);
        recordToCreate.setWindSpeed(5.2);
        recordToCreate.setWindDeg(180);
        recordToCreate.setCityId(cityId);
        recordToCreate.setTimestamp(LocalDateTime.now());
        
        ResponseEntity<RecordDTO> createResponse = restTemplate.postForEntity(recordUrl, recordToCreate, RecordDTO.class);
        
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getId());
        assertEquals(cityId, createResponse.getBody().getCityId());
        
        Long recordId = createResponse.getBody().getId();
        
        // Get the record by ID
        ResponseEntity<RecordDTO> getResponse = restTemplate.getForEntity(recordUrl + "/" + recordId, RecordDTO.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(recordId, getResponse.getBody().getId());
        assertEquals(10.5, getResponse.getBody().getMinTemperature());
        assertEquals(22.3, getResponse.getBody().getMaxTemperature());
        
        // Get records by city ID
        ResponseEntity<RecordDTO[]> getByCityResponse = restTemplate.getForEntity(recordUrl + "/city/" + cityId, RecordDTO[].class);
        assertEquals(HttpStatus.OK, getByCityResponse.getStatusCode());
        assertTrue(getByCityResponse.getBody().length > 0);
        
        // Get latest record for city
        ResponseEntity<RecordDTO> getLatestResponse = restTemplate.getForEntity(recordUrl + "/city/" + cityId + "/latest", RecordDTO.class);
        assertEquals(HttpStatus.OK, getLatestResponse.getStatusCode());
        assertEquals(cityId, getLatestResponse.getBody().getCityId());
        
        // Update the record
        RecordDTO updatedRecord = new RecordDTO(
                recordId, 11.0, 23.0, 1015, 60, 4.8, 190, 
                LocalDateTime.now(), cityId, "Test City");
        
        ResponseEntity<RecordDTO> updateResponse = restTemplate.exchange(
                recordUrl + "/" + recordId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedRecord),
                RecordDTO.class);
        
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals(11.0, updateResponse.getBody().getMinTemperature());
        assertEquals(23.0, updateResponse.getBody().getMaxTemperature());
        
        // Delete the record
        restTemplate.delete(recordUrl + "/" + recordId);
        
        // Verify deletion
        ResponseEntity<RecordDTO> getDeletedResponse = restTemplate.getForEntity(recordUrl + "/" + recordId, RecordDTO.class);
        assertEquals(HttpStatus.NOT_FOUND, getDeletedResponse.getStatusCode());
        
        // Clean up - delete the city and country
        restTemplate.delete(cityUrl + "/" + cityId);
        restTemplate.delete(countryUrl + "/" + countryId);
    }
}
