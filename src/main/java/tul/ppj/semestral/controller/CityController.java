package tul.ppj.semestral.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tul.ppj.semestral.dto.CityDTO;
import tul.ppj.semestral.service.CityService;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {
    private static final Logger logger = LoggerFactory.getLogger(CityController.class);

    private final CityService cityService;

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public ResponseEntity<List<CityDTO>> getAllCities() {
        logger.info("REST request to get all cities");
        List<CityDTO> cities = cityService.getAllCities();
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CityDTO> getCityById(@PathVariable Long id) {
        logger.info("REST request to get city with id: {}", id);
        return cityService.getCityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/country/{countryId}")
    public ResponseEntity<List<CityDTO>> getCitiesByCountryId(@PathVariable Long countryId) {
        logger.info("REST request to get cities for country id: {}", countryId);
        List<CityDTO> cities = cityService.getCitiesByCountryId(countryId);
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/country/name/{countryName}")
    public ResponseEntity<List<CityDTO>> getCitiesByCountryName(@PathVariable String countryName) {
        logger.info("REST request to get cities for country name: {}", countryName);
        List<CityDTO> cities = cityService.getCitiesByCountryName(countryName);
        return ResponseEntity.ok(cities);
    }

    @PostMapping
    public ResponseEntity<CityDTO> createCity(@RequestBody CityDTO cityDTO) {
        logger.info("REST request to create city: {} in country id: {}", cityDTO.getName(), cityDTO.getCountryId());
        return cityService.createCity(cityDTO)
                .map(city -> ResponseEntity.status(HttpStatus.CREATED).body(city))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CityDTO> updateCity(@PathVariable Long id, @RequestBody CityDTO cityDTO) {
        logger.info("REST request to update city with id: {}", id);
        return cityService.updateCity(id, cityDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        logger.info("REST request to delete city with id: {}", id);
        boolean deleted = cityService.deleteCity(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
