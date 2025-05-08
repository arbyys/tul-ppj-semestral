package tul.ppj.semestral.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tul.ppj.semestral.dto.RecordDTO;
import tul.ppj.semestral.model.City;
import tul.ppj.semestral.repository.CityRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class WeatherApiService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherApiService.class);

    @Value("${weather.api.key:}")
    private String apiKey;

    @Value("${weather.api.url:https://api.openweathermap.org/data/2.5/weather}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final CityRepository cityRepository;
    private final RecordService recordService;
    private final Random random = new Random();

    @Autowired
    public WeatherApiService(CityRepository cityRepository, RecordService recordService) {
        this.cityRepository = cityRepository;
        this.recordService = recordService;
        this.restTemplate = new RestTemplate();
    }

    public Optional<RecordDTO> fetchWeatherDataForCity(Long cityId) {
        logger.info("Fetching weather data for city id: {}", cityId);

        Optional<City> cityOpt = cityRepository.findById(cityId);
        if (cityOpt.isEmpty()) {
            logger.warn("City with id {} not found", cityId);
            return Optional.empty();
        }

        City city = cityOpt.get();

        // check if API key is available, mock otherwise
        if (apiKey == null || apiKey.isEmpty()) {
            logger.warn("No API key available, generating random weather data");
            return generateRandomWeatherData(city);
        }

        try {
            String url = String.format("%s?lat=%s&lon=%s&appid=%s&units=metric",
                    apiUrl, city.getLatitude(), city.getLongitude(), apiKey);

            ResponseEntity<WeatherApiResponse> response = restTemplate.getForEntity(url, WeatherApiResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                WeatherApiResponse weatherData = response.getBody();

                RecordDTO recordDTO = new RecordDTO();
                recordDTO.setCityId(cityId);
                recordDTO.setCityName(city.getName());
                recordDTO.setMinTemperature(weatherData.getMain().getTemp_min());
                recordDTO.setMaxTemperature(weatherData.getMain().getTemp_max());
                recordDTO.setPressure(weatherData.getMain().getPressure());
                recordDTO.setHumidity(weatherData.getMain().getHumidity());
                recordDTO.setWindSpeed(weatherData.getWind().getSpeed());
                recordDTO.setWindDeg(weatherData.getWind().getDeg());
                recordDTO.setTimestamp(LocalDateTime.now());

                return recordService.createRecord(recordDTO);
            } else {
                logger.error("Failed to fetch weather data: {}", response.getStatusCode());
                return generateRandomWeatherData(city);
            }
        } catch (Exception e) {
            logger.error("Error fetching weather data: {}", e.getMessage(), e);
            return generateRandomWeatherData(city);
        }
    }

    /**
     * generates mock random weather data for testing when API key isn't available
     */
    private Optional<RecordDTO> generateRandomWeatherData(City city) {
        logger.info("Generating random weather data for city: {}", city.getName());

        RecordDTO recordDTO = new RecordDTO();
        recordDTO.setCityId(city.getId());
        recordDTO.setCityName(city.getName());

        // generate realistic random weather data
        double baseTemp = 15.0 + (random.nextDouble() * 10.0 - 5.0); // base temperature around 15°C ±5°C
        recordDTO.setMinTemperature(baseTemp - random.nextDouble() * 5.0);
        recordDTO.setMaxTemperature(baseTemp + random.nextDouble() * 5.0);
        recordDTO.setPressure(1000 + random.nextInt(30));
        recordDTO.setHumidity(40 + random.nextInt(50));
        recordDTO.setWindSpeed(random.nextDouble() * 10.0);
        recordDTO.setWindDeg(random.nextInt(360));
        recordDTO.setTimestamp(LocalDateTime.now());

        return recordService.createRecord(recordDTO);
    }

    // classes to map the OpenWeatherMap API response
    private static class WeatherApiResponse {
        private MainData main;
        private WindData wind;

        public MainData getMain() {
            return main;
        }

        public void setMain(MainData main) {
            this.main = main;
        }

        public WindData getWind() {
            return wind;
        }

        public void setWind(WindData wind) {
            this.wind = wind;
        }
    }

    private static class MainData {
        private double temp;
        private double temp_min;
        private double temp_max;
        private int pressure;
        private int humidity;

        public double getTemp() {
            return temp;
        }

        public void setTemp(double temp) {
            this.temp = temp;
        }

        public double getTemp_min() {
            return temp_min;
        }

        public void setTemp_min(double temp_min) {
            this.temp_min = temp_min;
        }

        public double getTemp_max() {
            return temp_max;
        }

        public void setTemp_max(double temp_max) {
            this.temp_max = temp_max;
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
    }

    private static class WindData {
        private double speed;
        private int deg;

        public double getSpeed() {
            return speed;
        }

        public void setSpeed(double speed) {
            this.speed = speed;
        }

        public int getDeg() {
            return deg;
        }

        public void setDeg(int deg) {
            this.deg = deg;
        }
    }
}
