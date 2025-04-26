package tul.ppj.semestral.dto;

public class CityDTO {
    private Long id;
    private String name;
    private Long countryId;
    private String countryName;
    private Long population;
    private Double latitude;
    private Double longitude;

    // Constructors
    public CityDTO() {
    }

    public CityDTO(Long id, String name, Long countryId, String countryName, Long population, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.countryId = countryId;
        this.countryName = countryName;
        this.population = population;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Long getPopulation() {
        return population;
    }

    public void setPopulation(Long population) {
        this.population = population;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
