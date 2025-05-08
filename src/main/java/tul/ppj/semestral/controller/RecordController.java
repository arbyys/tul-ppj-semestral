package tul.ppj.semestral.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tul.ppj.semestral.dto.RecordDTO;
import tul.ppj.semestral.dto.WeatherStatisticsDTO;
import tul.ppj.semestral.service.RecordService;
import tul.ppj.semestral.service.WeatherApiService;

import java.util.List;

@RestController
@RequestMapping("/api/records")
public class RecordController {
    private static final Logger logger = LoggerFactory.getLogger(RecordController.class);

    private final RecordService recordService;
    private final WeatherApiService weatherApiService;

    @Autowired
    public RecordController(RecordService recordService, WeatherApiService weatherApiService) {
        this.recordService = recordService;
        this.weatherApiService = weatherApiService;
    }

    @GetMapping
    public ResponseEntity<List<RecordDTO>> getAllRecords() {
        logger.info("HTTP request to get all weather records");
        List<RecordDTO> records = recordService.getAllRecords();
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecordDTO> getRecordById(@PathVariable Long id) {
        logger.info("HTTP request to get weather record with id: {}", id);
        return recordService.getRecordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/city/{cityId}")
    public ResponseEntity<List<RecordDTO>> getRecordsByCityId(@PathVariable Long cityId) {
        logger.info("HTTP request to get weather records for city id: {}", cityId);
        List<RecordDTO> records = recordService.getRecordsByCityId(cityId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/city/{cityId}/latest")
    public ResponseEntity<RecordDTO> getLatestRecordForCity(@PathVariable Long cityId) {
        logger.info("HTTP request to get latest weather record for city id: {}", cityId);
        return recordService.getLatestRecordForCity(cityId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/city/{cityId}/statistics/{period}")
    public ResponseEntity<WeatherStatisticsDTO> getStatisticsForCity(
            @PathVariable Long cityId,
            @PathVariable String period) {
        logger.info("HTTP request to get {} statistics for city id: {}", period, cityId);
        return recordService.getStatisticsForCity(cityId, period)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RecordDTO> createRecord(@RequestBody RecordDTO recordDTO) {
        logger.info("HTTP request to create weather record for city id: {}", recordDTO.getCityId());
        return recordService.createRecord(recordDTO)
                .map(record -> ResponseEntity.status(HttpStatus.CREATED).body(record))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecordDTO> updateRecord(@PathVariable Long id, @RequestBody RecordDTO recordDTO) {
        logger.info("HTTP request to update weather record with id: {}", id);
        return recordService.updateRecord(id, recordDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        logger.info("HTTP request to delete weather record with id: {}", id);
        boolean deleted = recordService.deleteRecord(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/fetch/city/{cityId}")
    public ResponseEntity<RecordDTO> fetchWeatherDataForCity(@PathVariable Long cityId) {
        logger.info("HTTP request to fetch weather data for city id: {}", cityId);
        return weatherApiService.fetchWeatherDataForCity(cityId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
