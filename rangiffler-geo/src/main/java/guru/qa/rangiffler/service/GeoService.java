package guru.qa.rangiffler.service;

import guru.qa.rangiffler.data.CountryEntity;
import guru.qa.rangiffler.data.repository.CountryRepository;
import java.util.List;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ParametersAreNonnullByDefault
public class GeoService {

  private final CountryRepository countryRepository;

  @Autowired
  public GeoService(CountryRepository countryRepository) {
    this.countryRepository = countryRepository;
  }

  public List<CountryEntity> countries() {
    return countryRepository.findAll();
  }

  public Optional<CountryEntity > getCountry(String code) {
    return countryRepository.findByCode(code);
  }
}
