package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.data.PhotoEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@ParametersAreNonnullByDefault
public interface PhotoRepository extends JpaRepository<PhotoEntity, UUID> {

  @Nonnull
  @Query("SELECT p FROM PhotoEntity p")
  List<PhotoEntity> findAll();

  @Nonnull
  Page<PhotoEntity> findAllByUserId(UUID uuid, Pageable pageable);

  @Nonnull
  Optional<PhotoEntity> findByIdAndUserId(UUID id, UUID userId);

  void deleteByUserIdAndId(UUID userId, UUID id);
}
