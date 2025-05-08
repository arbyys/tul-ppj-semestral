package tul.ppj.semestral.model;

import jakarta.persistence.*;
//import lombok.Data;

//@Data
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

    // constructors
    public Country() {
    }

    public Country(String name, String isoCode, Long population, String continent) {
        this.name = name;
        this.isoCode = isoCode;
        this.population = population;
        this.continent = continent;
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public Long getPopulation() {
        return population;
    }

    public void setPopulation(Long population) {
        this.population = population;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }
}
