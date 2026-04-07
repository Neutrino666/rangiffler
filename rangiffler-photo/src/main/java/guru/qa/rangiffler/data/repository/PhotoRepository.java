package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.data.PhotoEntity;
import guru.qa.rangiffler.data.projection.UserCountrySum;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@ParametersAreNonnullByDefault
public interface PhotoRepository extends JpaRepository<PhotoEntity, UUID> {

  @Nonnull
  @Query("SELECT p FROM PhotoEntity p")
  List<PhotoEntity> findAll();

  @Nonnull
  Page<PhotoEntity> findAllByUserId(UUID uuid, Pageable pageable);

  @Nonnull
  Page<PhotoEntity> findAllByUserIdIn(List<UUID> ids, Pageable pageable);

  @Nonnull
  Optional<PhotoEntity> findByIdAndUserId(UUID id, UUID userId);

  @Nonnull
  Optional<PhotoEntity> findById(UUID id);

  void deleteByUserIdAndId(UUID userId, UUID id);

  @Nonnull
  @Query("""
      SELECT new guru.qa.rangiffler.data.projection.UserCountrySum(COUNT(p.id), p.country)
      FROM PhotoEntity p
      WHERE p.userId in :ids
      GROUP BY p.country
      """)
  List<UserCountrySum> statisticByUser(@Param("ids") List<UUID> ids);
}
