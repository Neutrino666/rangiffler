package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.data.UserEntity;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@ParametersAreNonnullByDefault
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

  @Nonnull
  Optional<UserEntity> findByUsername(String username);

  Page<UserEntity> findByUsernameNot(String username, Pageable pageable);

  @Query("select u from UserEntity u where u.username <> :username" +
      " and (u.username like %:searchQuery% or u.firstname like %:searchQuery% or u.surname like %:searchQuery%)")
  Page<UserEntity> findByUsernameNotAndSearchQuery(
      @Param("username") String username,
      @Param("searchQuery") String searchQuery,
      Pageable pageable
  );
}
