package tul.ppj.semestral.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tul.ppj.semestral.dto.CityDTO;
import tul.ppj.semestral.model.City;
import tul.ppj.semestral.model.Country;
import tul.ppj.semestral.repository.CityRepository;
import tul.ppj.semestral.repository.CountryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CityService {
    private static final Logger logger = LoggerFactory.getLogger(CityService.class);
    
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    @Autowired
    public CityService(CityRepository cityRepository, CountryRepository countryRepository) {
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
    }

    public List<CityDTO> getAllCities() {
        logger.info("Fetching all cities");
        return cityRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<CityDTO> getCityById(Long id) {
        logger.info("Fetching city with id: {}", id);
        return cityRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<CityDTO> getCitiesByCountryId(Long countryId) {
        logger.info("Fetching cities for country id: {}", countryId);
        return cityRepository.findByCountryId(countryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CityDTO> getCitiesByCountryName(String countryName) {
        logger.info("Fetching cities for country name: {}", countryName);
        return cityRepository.findByCountryName(countryName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<CityDTO> createCity(CityDTO cityDTO) {
        logger.info("Creating new city: {} in country id: {}", cityDTO.getName(), cityDTO.getCountryId());
        
        Optional<Country> countryOpt = countryRepository.findById(cityDTO.getCountryId());
        if (countryOpt.isEmpty()) {
            logger.warn("Country with id {} not found", cityDTO.getCountryId());
            return Optional.empty();
        }
        
        City city = convertToEntity(cityDTO, countryOpt.get());
        City savedCity = cityRepository.save(city);
        return Optional.of(convertToDTO(savedCity));
    }

    public Optional<CityDTO> updateCity(Long id, CityDTO cityDTO) {
        logger.info("Updating city with id: {}", id);
        if (!cityRepository.existsById(id)) {
            logger.warn("City with id {} not found", id);
            return Optional.empty();
        }
        
        Optional<Country> countryOpt = countryRepository.findById(cityDTO.getCountryId());
        if (countryOpt.isEmpty()) {
            logger.warn("Country with id {} not found", cityDTO.getCountryId());
            return Optional.empty();
        }
        
        City city = convertToEntity(cityDTO, countryOpt.get());
        city.setId(id);
        City updatedCity = cityRepository.save(city);
        return Optional.of(convertToDTO(updatedCity));
    }

    public boolean deleteCity(Long id) {
        logger.info("Deleting city with id: {}", id);
        if (!cityRepository.existsById(id)) {
            logger.warn("City with id {} not found", id);
            return false;
        }
        
        cityRepository.deleteById(id);
        return true;
    }

    // Helper methods for DTO conversion
    private CityDTO convertToDTO(City city) {
        return new CityDTO(
                city.getId(),
                city.getName(),
                city.getCountry().getId(),
                city.getCountry().getName(),
                city.getPopulation(),
                city.getLatitude(),
                city.getLongitude()
        );
    }

    private City convertToEntity(CityDTO cityDTO, Country country) {
        City city = new City();
        // Skip setting ID for new entities
        if (cityDTO.getId() != null) {
            city.setId(cityDTO.getId());
        }
        city.setName(cityDTO.getName());
        city.setCountry(country);
        city.setPopulation(cityDTO.getPopulation());
        city.setLatitude(cityDTO.getLatitude());
        city.setLongitude(cityDTO.getLongitude());
        return city;
    }
}
