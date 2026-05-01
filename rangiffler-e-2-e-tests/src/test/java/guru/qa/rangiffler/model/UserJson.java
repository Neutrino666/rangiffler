package guru.qa.rangiffler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.type.FriendStatus;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserJson {

  @JsonProperty("id")
  UUID id;
  @JsonProperty("username")
  String username;
  @JsonProperty("firstname")
  String firstname;
  @JsonProperty("surname")
  String surname;
  @JsonProperty("avatar")
  String avatar;
  @JsonProperty("country_code")
  String countryCode;
  @JsonProperty("friendshipStatus")
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
        countryCode,
        friendshipStatus,
        testData
    );
  }
}