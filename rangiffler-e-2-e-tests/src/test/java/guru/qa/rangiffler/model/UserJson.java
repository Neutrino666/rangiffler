package guru.qa.rangiffler.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import guru.qa.type.FriendStatus;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@ParametersAreNonnullByDefault
public final class UserJson {

  UUID id;
  String username;
  String firstname;
  String surname;
  String avatar;
  CountryJson country;
  @Nullable
  FriendStatus friendshipStatus;
  @Nullable
  TestData testData;

  public UserJson addTestData(TestData testData) {
    return new UserJson(
        id,
        username,
        firstname,
        surname,
        avatar,
        country,
        friendshipStatus,
        testData
    );
  }
}