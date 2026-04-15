package guru.qa.rangiffler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import guru.qa.rangiffler.data.Authority;
import guru.qa.rangiffler.data.AuthorityEntity;
import guru.qa.rangiffler.data.UserEntity;
import guru.qa.rangiffler.data.repository.UserRepository;
import guru.qa.rangiffler.service.DatabaseUserDetailsService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
public class DatabaseUserDetailsServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private DatabaseUserDetailsService databaseUserDetailsService;

  private UserEntity userEntity;

  private final String username = "unitTest";
  private final UUID userId = UUID.randomUUID();
  private final String password = "12345";

  @BeforeEach
  void before() {
    userEntity = new UserEntity();
    userEntity.setId(userId);
    userEntity.setUsername(username);
    userEntity.setPassword(password);
    userEntity.setEnabled(true);
    userEntity.setAccountNonExpired(true);
    userEntity.setAccountNonLocked(true);
    userEntity.setCredentialsNonExpired(true);
    final List<AuthorityEntity> authorityEntities = Stream.of(Authority.values())
        .map(a -> {
          AuthorityEntity ae = new AuthorityEntity();
          ae.setAuthority(a);
          return ae;
        })
        .toList();
    userEntity.setAuthorities(authorityEntities);
  }

  @Test
  void shouldIsPresentUserByUsername() {
    final List<String> expectedAuthority = Stream.of(Authority.values()).map(Authority::name).toList();

    when(userRepository.findByUsername(username))
        .thenReturn(Optional.of(userEntity));
    final UserDetails userDetails = databaseUserDetailsService.loadUserByUsername(username);

    assertThat(userDetails)
        .hasNoNullFieldsOrProperties()
        .hasFieldOrPropertyWithValue("Username", username)
        .hasFieldOrPropertyWithValue("Password", password)
        .hasFieldOrPropertyWithValue("AccountNonExpired", true)
        .hasFieldOrPropertyWithValue("AccountNonLocked", true)
        .hasFieldOrPropertyWithValue("CredentialsNonExpired", true)
        .hasFieldOrPropertyWithValue("Enabled", true);

    assertThat(userDetails.getAuthorities())
        .extracting(GrantedAuthority::getAuthority)
        .containsExactlyInAnyOrderElementsOf(expectedAuthority);
  }

  @Test
  void shouldThrowUserNotFoundException() {
    final String notExistUsername = "notFound";
    when(userRepository.findByUsername(notExistUsername))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> databaseUserDetailsService.loadUserByUsername(notExistUsername))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessage("Username: `" + notExistUsername + "` not found");
  }
}


