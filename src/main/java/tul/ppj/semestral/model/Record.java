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
    private City city;
}
