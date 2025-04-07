package tul.ppj.semestral.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "countries")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = true, length = 3)
    private String isoCode;

    @Column(nullable = true)
    private Long population;

    @Column(nullable = true)
    private String continent;
}
