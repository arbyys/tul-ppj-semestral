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

        try {
            // use the new method that directly returns a statistics object
            WeatherStatisticsDTO statistics = recordRepository.getStatistics(cityId, period, startDate, endDate);

            if (statistics == null) {
                logger.warn("No data available for the specified period");
                return Optional.empty();
            }

            logger.info("Calculated statistics: minTemp={}, maxTemp={}, pressure={}, humidity={}, windSpeed={}",
                    statistics.getAvgMinTemperature(), statistics.getAvgMaxTemperature(),
                    statistics.getAvgPressure(), statistics.getAvgHumidity(), statistics.getAvgWindSpeed());

            return Optional.of(statistics);
        } catch (Exception e) {
            logger.error("Error calculating statistics: {}", e.getMessage(), e);

            // fallback to the original method in case of error
            try {
                Number[] averages = recordRepository.getAveragesByCityIdAndDateRange(cityId, startDate, endDate);

                if (averages == null || averages.length == 0) {
                    logger.warn("No data available for the specified period");
                    return Optional.empty();
                }

                logger.info("Received averages array with length: {}", averages.length);
                for (int i = 0; i < averages.length; i++) {
                    logger.info("averages[{}] = {}, type: {}", i, averages[i],
                            (averages[i] != null ? averages[i].getClass().getName() : "null"));
                }

                // safely convert values to double with index checking
                double avgMinTemp = averages.length > 0 ? safeToDouble(averages[0]) : 0.0;
                double avgMaxTemp = averages.length > 1 ? safeToDouble(averages[1]) : 0.0;
                double avgPressure = averages.length > 2 ? safeToDouble(averages[2]) : 0.0;
                double avgHumidity = averages.length > 3 ? safeToDouble(averages[3]) : 0.0;
                double avgWindSpeed = averages.length > 4 ? safeToDouble(averages[4]) : 0.0;

                logger.info("Calculated statistics (fallback): minTemp={}, maxTemp={}, pressure={}, humidity={}, windSpeed={}",
                        avgMinTemp, avgMaxTemp, avgPressure, avgHumidity, avgWindSpeed);

                WeatherStatisticsDTO statistics = new WeatherStatisticsDTO(
                        cityId,
                        cityOpt.get().getName(),
                        period,
                        avgMinTemp,
                        avgMaxTemp,
                        avgPressure,
                        avgHumidity,
                        avgWindSpeed
                );

                return Optional.of(statistics);
            } catch (Exception ex) {
                logger.error("Fallback method also failed: {}", ex.getMessage(), ex);
                return Optional.empty();
            }
        }
    }

    private double safeToDouble(Object obj) {
        if (obj == null) {
            return 0.0;
        }

        try {
            if (obj instanceof Number) {
                return ((Number) obj).doubleValue();
            } else if (obj instanceof String) {
                return Double.parseDouble((String) obj);
            } else if (obj.getClass().isArray() && obj.getClass().getComponentType().equals(Object.class)) {
                // get first element if param is an array
                Object[] array = (Object[]) obj;
                if (array.length > 0 && array[0] != null) {
                    return safeToDouble(array[0]);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to convert {} to double: {}", obj, e.getMessage());
        }

        return 0.0;
    }

    // helper methods for DTO conversion
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
        // skip setting ID for new entities
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
