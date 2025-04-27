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
import tul.ppj.semestral.dto.RecordDTO;
import tul.ppj.semestral.dto.WeatherStatisticsDTO;
import tul.ppj.semestral.service.RecordService;
import tul.ppj.semestral.service.WeatherApiService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecordController.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.sql.init.mode=never"})
public class RecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecordService recordService;

    @MockBean
    private WeatherApiService weatherApiService;

    private RecordDTO pragueRecord;
    private RecordDTO berlinRecord;
    private LocalDateTime now;

    @BeforeEach
    public void setup() {
        now = LocalDateTime.now();
        pragueRecord = new RecordDTO(1L, 10.5, 22.3, 1013, 65, 5.2, 180, now, 1L, "Prague");
        berlinRecord = new RecordDTO(2L, 8.0, 18.5, 1010, 70, 6.0, 200, now, 3L, "Berlin");
    }

    @Test
    public void testGetAllRecords() throws Exception {
        List<RecordDTO> records = Arrays.asList(pragueRecord, berlinRecord);
        when(recordService.getAllRecords()).thenReturn(records);

        mockMvc.perform(get("/api/records"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].cityName").value("Prague"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].cityName").value("Berlin"));
    }

    @Test
    public void testGetRecordById_Found() throws Exception {
        when(recordService.getRecordById(1L)).thenReturn(Optional.of(pragueRecord));

        mockMvc.perform(get("/api/records/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cityName").value("Prague"))
                .andExpect(jsonPath("$.minTemperature").value(10.5))
                .andExpect(jsonPath("$.maxTemperature").value(22.3));
    }

    @Test
    public void testGetRecordById_NotFound() throws Exception {
        when(recordService.getRecordById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/records/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetRecordsByCityId() throws Exception {
        List<RecordDTO> pragueRecords = Arrays.asList(pragueRecord);
        when(recordService.getRecordsByCityId(1L)).thenReturn(pragueRecords);

        mockMvc.perform(get("/api/records/city/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].cityName").value("Prague"))
                .andExpect(jsonPath("$[0].cityId").value(1));
    }

    @Test
    public void testGetLatestRecordForCity_Found() throws Exception {
        when(recordService.getLatestRecordForCity(1L)).thenReturn(Optional.of(pragueRecord));

        mockMvc.perform(get("/api/records/city/1/latest"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cityName").value("Prague"));
    }

    @Test
    public void testGetLatestRecordForCity_NotFound() throws Exception {
        when(recordService.getLatestRecordForCity(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/records/city/99/latest"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetStatisticsForCity_Found() throws Exception {
        WeatherStatisticsDTO statistics = new WeatherStatisticsDTO(
                1L, "Prague", "week", 9.5, 21.0, 1012, 68, 5.5);

        when(recordService.getStatisticsForCity(1L, "week")).thenReturn(Optional.of(statistics));

        mockMvc.perform(get("/api/records/city/1/statistics/week"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cityId").value(1))
                .andExpect(jsonPath("$.cityName").value("Prague"))
                .andExpect(jsonPath("$.period").value("week"))
                .andExpect(jsonPath("$.avgMinTemperature").value(9.5));
    }

    @Test
    public void testGetStatisticsForCity_NotFound() throws Exception {
        when(recordService.getStatisticsForCity(99L, "week")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/records/city/99/statistics/week"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateRecord_Success() throws Exception {
        RecordDTO newRecord = new RecordDTO(null, 11.0, 23.0, 1015, 60, 4.8, 190, now, 1L, null);
        RecordDTO createdRecord = new RecordDTO(3L, 11.0, 23.0, 1015, 60, 4.8, 190, now, 1L, "Prague");

        when(recordService.createRecord(any(RecordDTO.class))).thenReturn(Optional.of(createdRecord));

        mockMvc.perform(post("/api/records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRecord)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.cityName").value("Prague"))
                .andExpect(jsonPath("$.minTemperature").value(11.0))
                .andExpect(jsonPath("$.maxTemperature").value(23.0));
    }

    @Test
    public void testCreateRecord_BadRequest() throws Exception {
        RecordDTO invalidRecord = new RecordDTO(null, 0.0, 0.0, 0, 0, 0.0, 0, now, 99L, null);

        when(recordService.createRecord(any(RecordDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRecord)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateRecord_Success() throws Exception {
        RecordDTO updatedRecord = new RecordDTO(1L, 11.0, 23.0, 1015, 60, 4.8, 190, now, 1L, "Prague");

        when(recordService.updateRecord(eq(1L), any(RecordDTO.class))).thenReturn(Optional.of(updatedRecord));

        mockMvc.perform(put("/api/records/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRecord)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.minTemperature").value(11.0))
                .andExpect(jsonPath("$.maxTemperature").value(23.0));
    }

    @Test
    public void testUpdateRecord_NotFound() throws Exception {
        RecordDTO nonExistentRecord = new RecordDTO(99L, 0.0, 0.0, 0, 0, 0.0, 0, now, 1L, "Prague");

        when(recordService.updateRecord(eq(99L), any(RecordDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/records/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistentRecord)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteRecord_Success() throws Exception {
        when(recordService.deleteRecord(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/records/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteRecord_NotFound() throws Exception {
        when(recordService.deleteRecord(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/records/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFetchWeatherDataForCity_Success() throws Exception {
        RecordDTO fetchedRecord = new RecordDTO(4L, 12.0, 24.0, 1016, 55, 3.5, 170, now, 1L, "Prague");

        when(weatherApiService.fetchWeatherDataForCity(1L)).thenReturn(Optional.of(fetchedRecord));

        mockMvc.perform(get("/api/records/fetch/city/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.cityName").value("Prague"))
                .andExpect(jsonPath("$.minTemperature").value(12.0))
                .andExpect(jsonPath("$.maxTemperature").value(24.0));
    }

    @Test
    public void testFetchWeatherDataForCity_NotFound() throws Exception {
        when(weatherApiService.fetchWeatherDataForCity(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/records/fetch/city/99"))
                .andExpect(status().isNotFound());
    }
}
