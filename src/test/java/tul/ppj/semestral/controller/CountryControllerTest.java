package tul.ppj.semestral.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
public class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CountryService countryService;

    @Test
    public void testGetAllCountries() throws Exception {
        CountryDTO country1 = new CountryDTO(1L, "Czech Republic", "CZE", 10000000L, "Europe");
        CountryDTO country2 = new CountryDTO(2L, "Germany", "DEU", 83000000L, "Europe");
        
        when(countryService.getAllCountries()).thenReturn(Arrays.asList(country1, country2));
        
        mockMvc.perform(get("/api/countries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Czech Republic"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Germany"));
    }

    @Test
    public void testGetCountryById() throws Exception {
        CountryDTO country = new CountryDTO(1L, "Czech Republic", "CZE", 10000000L, "Europe");
        
        when(countryService.getCountryById(1L)).thenReturn(Optional.of(country));
        when(countryService.getCountryById(99L)).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/countries/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Czech Republic"));
                
        mockMvc.perform(get("/api/countries/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateCountry() throws Exception {
        CountryDTO countryToCreate = new CountryDTO(null, "France", "FRA", 67000000L, "Europe");
        CountryDTO createdCountry = new CountryDTO(3L, "France", "FRA", 67000000L, "Europe");
        
        when(countryService.createCountry(any(CountryDTO.class))).thenReturn(createdCountry);
        
        mockMvc.perform(post("/api/countries")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"France\",\"isoCode\":\"FRA\",\"population\":67000000,\"continent\":\"Europe\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("France"));
    }

    @Test
    public void testUpdateCountry() throws Exception {
        CountryDTO countryToUpdate = new CountryDTO(1L, "Czech Republic Updated", "CZE", 10500000L, "Europe");
        
        when(countryService.updateCountry(eq(1L), any(CountryDTO.class))).thenReturn(Optional.of(countryToUpdate));
        when(countryService.updateCountry(eq(99L), any(CountryDTO.class))).thenReturn(Optional.empty());
        
        mockMvc.perform(put("/api/countries/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1,\"name\":\"Czech Republic Updated\",\"isoCode\":\"CZE\",\"population\":10500000,\"continent\":\"Europe\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Czech Republic Updated"));
                
        mockMvc.perform(put("/api/countries/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":99,\"name\":\"Not Found\",\"isoCode\":\"NF\",\"population\":0,\"continent\":\"Unknown\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteCountry() throws Exception {
        when(countryService.deleteCountry(1L)).thenReturn(true);
        when(countryService.deleteCountry(99L)).thenReturn(false);
        
        mockMvc.perform(delete("/api/countries/1"))
                .andExpect(status().isNoContent());
                
        mockMvc.perform(delete("/api/countries/99"))
                .andExpect(status().isNotFound());
    }
}
