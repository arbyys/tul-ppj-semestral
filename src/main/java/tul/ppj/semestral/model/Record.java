package tul.ppj.semestral.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
//import lombok.Data;

//@Data
@Entity
@Table(name = "records")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double min_temperature;
    private double max_temperature;
    private int pressure;
    private int humidity;
    private double wind_speed;
    private int wind_deg;

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    // Constructors
    public Record() {
        this.timestamp = LocalDateTime.now();
    }

    public Record(double min_temperature, double max_temperature, int pressure, int humidity,
                 double wind_speed, int wind_deg, City city) {
        this.min_temperature = min_temperature;
        this.max_temperature = max_temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.wind_speed = wind_speed;
        this.wind_deg = wind_deg;
        this.timestamp = LocalDateTime.now();
        this.city = city;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getMin_temperature() {
        return min_temperature;
    }

    public void setMin_temperature(double min_temperature) {
        this.min_temperature = min_temperature;
    }

    public double getMax_temperature() {
        return max_temperature;
    }

    public void setMax_temperature(double max_temperature) {
        this.max_temperature = max_temperature;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public double getWind_speed() {
        return wind_speed;
    }

    public void setWind_speed(double wind_speed) {
        this.wind_speed = wind_speed;
    }

    public int getWind_deg() {
        return wind_deg;
    }

    public void setWind_deg(int wind_deg) {
        this.wind_deg = wind_deg;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
