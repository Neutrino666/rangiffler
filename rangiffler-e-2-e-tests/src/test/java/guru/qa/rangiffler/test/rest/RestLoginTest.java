package guru.qa.rangiffler.test.rest;

import static org.assertj.core.api.Assertions.assertThat;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.Token;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.UserJson;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@ParametersAreNonnullByDefault
@DisplayName("REST Login")
public final class RestLoginTest extends BaseRestTest {

  @User
  @ApiLogin
  @Test
  void login(@Token String token, final UserJson user) {
    assertThat(token).hasSizeGreaterThanOrEqualTo(730)
        .matches("^Bearer [\\w\\d_.-]{90,}$");

    System.out.println(user);
  }
}
