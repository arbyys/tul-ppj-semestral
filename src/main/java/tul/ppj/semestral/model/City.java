package tul.ppj.semestral.model;

import java.util.List;
import jakarta.persistence.*;
//import lombok.Data;

//@Data
@Entity
@Table(name = "cities")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Column(nullable = true)
    private Long population;

    @Column(nullable = true, precision = 10, scale = 6)
    private Double latitude;

    @Column(nullable = true, precision = 10, scale = 6)
    private Double longitude;

    @OneToMany(mappedBy = "city")
    private List<Record> records;
}
