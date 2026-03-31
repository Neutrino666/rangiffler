package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.data.CountryEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@ParametersAreNonnullByDefault
public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {

  @Nonnull
  @Query("SELECT c FROM CountryEntity c order by name")
  List<CountryEntity> findAll();

  @Nonnull
  Optional<CountryEntity> findByCode(String code);
}
