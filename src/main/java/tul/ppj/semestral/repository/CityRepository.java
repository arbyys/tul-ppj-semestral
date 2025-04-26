package tul.ppj.semestral.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tul.ppj.semestral.model.City;
import tul.ppj.semestral.model.Country;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findByNameAndCountry(String name, Country country);
    List<City> findByCountryId(Long countryId);
    List<City> findByCountryName(String countryName);
}
