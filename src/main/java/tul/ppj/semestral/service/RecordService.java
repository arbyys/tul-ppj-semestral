package tul.ppj.semestral.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tul.ppj.semestral.dto.RecordDTO;
import tul.ppj.semestral.dto.WeatherStatisticsDTO;
import tul.ppj.semestral.model.City;
import tul.ppj.semestral.model.Record;
import tul.ppj.semestral.repository.CityRepository;
import tul.ppj.semestral.repository.RecordRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecordService {
    private static final Logger logger = LoggerFactory.getLogger(RecordService.class);
    
    private final RecordRepository recordRepository;
    private final CityRepository cityRepository;

    @Autowired
    public RecordService(RecordRepository recordRepository, CityRepository cityRepository) {
        this.recordRepository = recordRepository;
        this.cityRepository = cityRepository;
    }

    public List<RecordDTO> getAllRecords() {
        logger.info("Fetching all weather records");
        return recordRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<RecordDTO> getRecordById(Long id) {
        logger.info("Fetching weather record with id: {}", id);
        return recordRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<RecordDTO> getRecordsByCityId(Long cityId) {
        logger.info("Fetching weather records for city id: {}", cityId);
        return recordRepository.findByCityId(cityId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<RecordDTO> getLatestRecordForCity(Long cityId) {
        logger.info("Fetching latest weather record for city id: {}", cityId);
        Record latestRecord = recordRepository.findLatestByCityId(cityId);
        return latestRecord != null ? Optional.of(convertToDTO(latestRecord)) : Optional.empty();
    }

    public Optional<RecordDTO> createRecord(RecordDTO recordDTO) {
        logger.info("Creating new weather record for city id: {}", recordDTO.getCityId());
        
        Optional<City> cityOpt = cityRepository.findById(recordDTO.getCityId());
        if (cityOpt.isEmpty()) {
            logger.warn("City with id {} not found", recordDTO.getCityId());
            return Optional.empty();
        }
        
        Record record = convertToEntity(recordDTO, cityOpt.get());
        Record savedRecord = recordRepository.save(record);
        return Optional.of(convertToDTO(savedRecord));
    }

    public Optional<RecordDTO> updateRecord(Long id, RecordDTO recordDTO) {
        logger.info("Updating weather record with id: {}", id);
        if (!recordRepository.existsById(id)) {
            logger.warn("Weather record with id {} not found", id);
            return Optional.empty();
        }
        
        Optional<City> cityOpt = cityRepository.findById(recordDTO.getCityId());
        if (cityOpt.isEmpty()) {
            logger.warn("City with id {} not found", recordDTO.getCityId());
            return Optional.empty();
        }
        
        Record record = convertToEntity(recordDTO, cityOpt.get());
        record.setId(id);
        Record updatedRecord = recordRepository.save(record);
        return Optional.of(convertToDTO(updatedRecord));
    }

    public boolean deleteRecord(Long id) {
        logger.info("Deleting weather record with id: {}", id);
        if (!recordRepository.existsById(id)) {
            logger.warn("Weather record with id {} not found", id);
            return false;
        }
        
        recordRepository.deleteById(id);
        return true;
    }

    public Optional<WeatherStatisticsDTO> getStatisticsForCity(Long cityId, String period) {
        logger.info("Calculating {} statistics for city id: {}", period, cityId);
        
        Optional<City> cityOpt = cityRepository.findById(cityId);
        if (cityOpt.isEmpty()) {
            logger.warn("City with id {} not found", cityId);
            return Optional.empty();
        }
        
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate;
        
        switch (period.toLowerCase()) {
            case "day":
                startDate = endDate.minusDays(1);
                break;
            case "week":
                startDate = endDate.minusWeeks(1);
                break;
            case "twoweeks":
                startDate = endDate.minusWeeks(2);
                break;
            default:
                logger.warn("Invalid period: {}", period);
                return Optional.empty();
        }
        
        Object[] averages = recordRepository.getAveragesByCityIdAndDateRange(cityId, startDate, endDate);
        
        if (averages == null || averages.length < 5 || averages[0] == null) {
            logger.warn("No data available for the specified period");
            return Optional.empty();
        }
        
        WeatherStatisticsDTO statistics = new WeatherStatisticsDTO(
                cityId,
                cityOpt.get().getName(),
                period,
                ((Number) averages[0]).doubleValue(),
                ((Number) averages[1]).doubleValue(),
                ((Number) averages[2]).doubleValue(),
                ((Number) averages[3]).doubleValue(),
                ((Number) averages[4]).doubleValue()
        );
        
        return Optional.of(statistics);
    }

    // Helper methods for DTO conversion
    private RecordDTO convertToDTO(Record record) {
        return new RecordDTO(
                record.getId(),
                record.getMin_temperature(),
                record.getMax_temperature(),
                record.getPressure(),
                record.getHumidity(),
                record.getWind_speed(),
                record.getWind_deg(),
                record.getTimestamp(),
                record.getCity().getId(),
                record.getCity().getName()
        );
    }

    private Record convertToEntity(RecordDTO recordDTO, City city) {
        Record record = new Record();
        // Skip setting ID for new entities
        if (recordDTO.getId() != null) {
            record.setId(recordDTO.getId());
        }
        record.setMin_temperature(recordDTO.getMinTemperature());
        record.setMax_temperature(recordDTO.getMaxTemperature());
        record.setPressure(recordDTO.getPressure());
        record.setHumidity(recordDTO.getHumidity());
        record.setWind_speed(recordDTO.getWindSpeed());
        record.setWind_deg(recordDTO.getWindDeg());
        record.setTimestamp(recordDTO.getTimestamp() != null ? recordDTO.getTimestamp() : LocalDateTime.now());
        record.setCity(city);
        return record;
    }
}
