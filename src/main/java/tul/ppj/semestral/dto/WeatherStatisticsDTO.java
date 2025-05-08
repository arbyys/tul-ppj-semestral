package tul.ppj.semestral.dto;

public class WeatherStatisticsDTO {
    private Long cityId;
    private String cityName;
    private String period; // "day", "week", "twoWeeks"
    private double avgMinTemperature;
    private double avgMaxTemperature;
    private double avgPressure;
    private double avgHumidity;
    private double avgWindSpeed;

    // constructors
    public WeatherStatisticsDTO() {
    }

    public WeatherStatisticsDTO(Long cityId, String cityName, String period, double avgMinTemperature,
                               double avgMaxTemperature, double avgPressure, double avgHumidity, double avgWindSpeed) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.period = period;
        this.avgMinTemperature = avgMinTemperature;
        this.avgMaxTemperature = avgMaxTemperature;
        this.avgPressure = avgPressure;
        this.avgHumidity = avgHumidity;
        this.avgWindSpeed = avgWindSpeed;
    }

    // getters and setters
    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public double getAvgMinTemperature() {
        return avgMinTemperature;
    }

    public void setAvgMinTemperature(double avgMinTemperature) {
        this.avgMinTemperature = avgMinTemperature;
    }

    public double getAvgMaxTemperature() {
        return avgMaxTemperature;
    }

    public void setAvgMaxTemperature(double avgMaxTemperature) {
        this.avgMaxTemperature = avgMaxTemperature;
    }

    public double getAvgPressure() {
        return avgPressure;
    }

    public void setAvgPressure(double avgPressure) {
        this.avgPressure = avgPressure;
    }

    public double getAvgHumidity() {
        return avgHumidity;
    }

    public void setAvgHumidity(double avgHumidity) {
        this.avgHumidity = avgHumidity;
    }

    public double getAvgWindSpeed() {
        return avgWindSpeed;
    }

    public void setAvgWindSpeed(double avgWindSpeed) {
        this.avgWindSpeed = avgWindSpeed;
    }
}
