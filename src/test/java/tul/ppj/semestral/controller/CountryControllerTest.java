package tul.ppj.semestral.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import tul.ppj.semestral.dto.CountryDTO;
import tul.ppj.semestral.service.CountryService;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CountryController.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.sql.init.mode=never"})
public class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CountryService countryService;

    private CountryDTO czechRepublic;
    private CountryDTO germany;

    @BeforeEach
    public void setup() {
        czechRepublic = new CountryDTO(1L, "Czech Republic", "CZE", 10000000L, "Europe");
        germany = new CountryDTO(2L, "Germany", "DEU", 83000000L, "Europe");
    }

    @Test
    public void testGetAllCountries() throws Exception {
        when(countryService.getAllCountries()).thenReturn(Arrays.asList(czechRepublic, germany));

        mockMvc.perform(get("/api/countries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Czech Republic"))
                .andExpect(jsonPath("$[0].isoCode").value("CZE"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Germany"))
                .andExpect(jsonPath("$[1].isoCode").value("DEU"));
    }

    @Test
    public void testGetCountryById_Found() throws Exception {
        when(countryService.getCountryById(1L)).thenReturn(Optional.of(czechRepublic));

        mockMvc.perform(get("/api/countries/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Czech Republic"))
                .andExpect(jsonPath("$.isoCode").value("CZE"));
    }

    @Test
    public void testGetCountryById_NotFound() throws Exception {
        when(countryService.getCountryById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/countries/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetCountryByName_Found() throws Exception {
        when(countryService.getCountryByName("Czech Republic")).thenReturn(Optional.of(czechRepublic));

        mockMvc.perform(get("/api/countries/name/Czech Republic"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Czech Republic"));
    }

    @Test
    public void testGetCountryByName_NotFound() throws Exception {
        when(countryService.getCountryByName("Unknown")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/countries/name/Unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateCountry() throws Exception {
        CountryDTO newCountry = new CountryDTO(null, "France", "FRA", 67000000L, "Europe");
        CountryDTO createdCountry = new CountryDTO(3L, "France", "FRA", 67000000L, "Europe");

        when(countryService.createCountry(any(CountryDTO.class))).thenReturn(createdCountry);

        mockMvc.perform(post("/api/countries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCountry)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("France"))
                .andExpect(jsonPath("$.isoCode").value("FRA"));
    }

    @Test
    public void testUpdateCountry_Success() throws Exception {
        CountryDTO updatedCountry = new CountryDTO(1L, "Czech Republic Updated", "CZE", 10100000L, "Europe");

        when(countryService.updateCountry(eq(1L), any(CountryDTO.class))).thenReturn(Optional.of(updatedCountry));

        mockMvc.perform(put("/api/countries/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCountry)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Czech Republic Updated"));
    }

    @Test
    public void testUpdateCountry_NotFound() throws Exception {
        CountryDTO nonExistentCountry = new CountryDTO(99L, "Not Found", "NF", 0L, "Unknown");

        when(countryService.updateCountry(eq(99L), any(CountryDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/countries/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistentCountry)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteCountry_Success() throws Exception {
        when(countryService.deleteCountry(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/countries/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteCountry_NotFound() throws Exception {
        when(countryService.deleteCountry(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/countries/99"))
                .andExpect(status().isNotFound());
    }
}
