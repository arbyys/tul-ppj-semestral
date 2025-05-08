package tul.ppj.semestral.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tul.ppj.semestral.dto.CountryDTO;
import tul.ppj.semestral.model.Country;
import tul.ppj.semestral.repository.CountryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CountryService {
    private static final Logger logger = LoggerFactory.getLogger(CountryService.class);

    private final CountryRepository countryRepository;

    @Autowired
    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public List<CountryDTO> getAllCountries() {
        logger.info("Fetching all countries");
        return countryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<CountryDTO> getCountryById(Long id) {
        logger.info("Fetching country with id: {}", id);
        return countryRepository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<CountryDTO> getCountryByName(String name) {
        logger.info("Fetching country with name: {}", name);
        return countryRepository.findByName(name)
                .map(this::convertToDTO);
    }

    public CountryDTO createCountry(CountryDTO countryDTO) {
        logger.info("Creating new country: {}", countryDTO.getName());
        Country country = convertToEntity(countryDTO);
        Country savedCountry = countryRepository.save(country);
        return convertToDTO(savedCountry);
    }

    public Optional<CountryDTO> updateCountry(Long id, CountryDTO countryDTO) {
        logger.info("Updating country with id: {}", id);
        if (!countryRepository.existsById(id)) {
            logger.warn("Country with id {} not found", id);
            return Optional.empty();
        }

        Country country = convertToEntity(countryDTO);
        country.setId(id);
        Country updatedCountry = countryRepository.save(country);
        return Optional.of(convertToDTO(updatedCountry));
    }

    public boolean deleteCountry(Long id) {
        logger.info("Deleting country with id: {}", id);
        if (!countryRepository.existsById(id)) {
            logger.warn("Country with id {} not found", id);
            return false;
        }

        countryRepository.deleteById(id);
        return true;
    }

    // helper methods for DTO conversion
    private CountryDTO convertToDTO(Country country) {
        return new CountryDTO(
                country.getId(),
                country.getName(),
                country.getIsoCode(),
                country.getPopulation(),
                country.getContinent()
        );
    }

    private Country convertToEntity(CountryDTO countryDTO) {
        Country country = new Country();
        // Skip setting ID for new entities
        if (countryDTO.getId() != null) {
            country.setId(countryDTO.getId());
        }
        country.setName(countryDTO.getName());
        country.setIsoCode(countryDTO.getIsoCode());
        country.setPopulation(countryDTO.getPopulation());
        country.setContinent(countryDTO.getContinent());
        return country;
    }
}
