package tul.ppj.semestral.dto;

public class CountryDTO {
    private Long id;
    private String name;
    private String isoCode;
    private Long population;
    private String continent;

    // Constructors
    public CountryDTO() {
    }

    public CountryDTO(Long id, String name, String isoCode, Long population, String continent) {
        this.id = id;
        this.name = name;
        this.isoCode = isoCode;
        this.population = population;
        this.continent = continent;
    }

    // Getters and Setters
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
