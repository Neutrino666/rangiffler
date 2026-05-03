package guru.qa.rangiffler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import guru.qa.type.FriendStatus;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Data
public final class UserJson {

  UUID id;
  String username;
  String firstname;
  String surname;
  String avatar;
  CountryJson country;
  @ToString.Exclude
  FriendStatus friendshipStatus;

  @JsonIgnore
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