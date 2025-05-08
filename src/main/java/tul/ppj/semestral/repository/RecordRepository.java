package tul.ppj.semestral.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tul.ppj.semestral.model.Record;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByCityId(Long cityId);

    List<Record> findByCityIdAndTimestampBetween(Long cityId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT r FROM Record r WHERE r.city.id = :cityId AND r.timestamp = (SELECT MAX(r2.timestamp) FROM Record r2 WHERE r2.city.id = :cityId)")
    Record findLatestByCityId(@Param("cityId") Long cityId);

    @Query("SELECT new tul.ppj.semestral.dto.WeatherStatisticsDTO(" +
           ":cityId, c.name, :period, " +
           "AVG(r.min_temperature), AVG(r.max_temperature), " +
           "AVG(r.pressure), AVG(r.humidity), AVG(r.wind_speed)) " +
           "FROM Record r JOIN r.city c " +
           "WHERE r.city.id = :cityId AND r.timestamp BETWEEN :startDate AND :endDate")
    tul.ppj.semestral.dto.WeatherStatisticsDTO getStatistics(
           @Param("cityId") Long cityId,
           @Param("period") String period,
           @Param("startDate") LocalDateTime startDate,
           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT AVG(r.min_temperature) as avgMinTemp, AVG(r.max_temperature) as avgMaxTemp, " +
           "AVG(r.pressure) as avgPressure, AVG(r.humidity) as avgHumidity, " +
           "AVG(r.wind_speed) as avgWindSpeed " +
           "FROM Record r WHERE r.city.id = :cityId AND r.timestamp BETWEEN :startDate AND :endDate")
    Number[] getAveragesByCityIdAndDateRange(@Param("cityId") Long cityId,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);
}
