package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.data.UserEntity;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.data.jpa.repository.JpaRepository;

@ParametersAreNonnullByDefault
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

  @Nonnull
  Optional<UserEntity> findByUsername(String username);
}
