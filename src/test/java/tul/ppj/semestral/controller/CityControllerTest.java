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
import tul.ppj.semestral.dto.CityDTO;
import tul.ppj.semestral.service.CityService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CityController.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.sql.init.mode=never"})
public class CityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CityService cityService;

    private CityDTO prague;
    private CityDTO brno;
    private CityDTO berlin;

    @BeforeEach
    public void setup() {
        prague = new CityDTO(1L, "Prague", 1L, "Czech Republic", 1300000L, 50.0755, 14.4378);
        brno = new CityDTO(2L, "Brno", 1L, "Czech Republic", 380000L, 49.1951, 16.6068);
        berlin = new CityDTO(3L, "Berlin", 2L, "Germany", 3600000L, 52.5200, 13.4050);
    }

    @Test
    public void testGetAllCities() throws Exception {
        List<CityDTO> cities = Arrays.asList(prague, brno, berlin);
        when(cityService.getAllCities()).thenReturn(cities);

        mockMvc.perform(get("/api/cities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Prague"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Brno"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].name").value("Berlin"));
    }

    @Test
    public void testGetCityById_Found() throws Exception {
        when(cityService.getCityById(1L)).thenReturn(Optional.of(prague));

        mockMvc.perform(get("/api/cities/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Prague"))
                .andExpect(jsonPath("$.countryId").value(1))
                .andExpect(jsonPath("$.countryName").value("Czech Republic"));
    }

    @Test
    public void testGetCityById_NotFound() throws Exception {
        when(cityService.getCityById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/cities/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetCitiesByCountryId() throws Exception {
        List<CityDTO> czechCities = Arrays.asList(prague, brno);
        when(cityService.getCitiesByCountryId(1L)).thenReturn(czechCities);

        mockMvc.perform(get("/api/cities/country/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Prague"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Brno"));
    }

    @Test
    public void testGetCitiesByCountryName() throws Exception {
        List<CityDTO> czechCities = Arrays.asList(prague, brno);
        when(cityService.getCitiesByCountryName("Czech Republic")).thenReturn(czechCities);

        mockMvc.perform(get("/api/cities/country/name/Czech Republic"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Prague"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Brno"));
    }

    @Test
    public void testCreateCity_Success() throws Exception {
        CityDTO newCity = new CityDTO(null, "Paris", 3L, null, 2200000L, 48.8566, 2.3522);
        CityDTO createdCity = new CityDTO(4L, "Paris", 3L, "France", 2200000L, 48.8566, 2.3522);

        when(cityService.createCity(any(CityDTO.class))).thenReturn(Optional.of(createdCity));

        mockMvc.perform(post("/api/cities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCity)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.name").value("Paris"))
                .andExpect(jsonPath("$.countryId").value(3))
                .andExpect(jsonPath("$.countryName").value("France"));
    }

    @Test
    public void testCreateCity_BadRequest() throws Exception {
        CityDTO newCity = new CityDTO(null, "Invalid City", 99L, null, 100000L, 0.0, 0.0);

        when(cityService.createCity(any(CityDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/cities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCity)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateCity_Success() throws Exception {
        CityDTO updatedCity = new CityDTO(1L, "Prague Updated", 1L, "Czech Republic", 1350000L, 50.0755, 14.4378);

        when(cityService.updateCity(eq(1L), any(CityDTO.class))).thenReturn(Optional.of(updatedCity));

        mockMvc.perform(put("/api/cities/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCity)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Prague Updated"))
                .andExpect(jsonPath("$.population").value(1350000));
    }

    @Test
    public void testUpdateCity_NotFound() throws Exception {
        CityDTO nonExistentCity = new CityDTO(99L, "Not Found", 1L, "Czech Republic", 0L, 0.0, 0.0);

        when(cityService.updateCity(eq(99L), any(CityDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/cities/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistentCity)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteCity_Success() throws Exception {
        when(cityService.deleteCity(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/cities/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteCity_NotFound() throws Exception {
        when(cityService.deleteCity(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/cities/99"))
                .andExpect(status().isNotFound());
    }
}
