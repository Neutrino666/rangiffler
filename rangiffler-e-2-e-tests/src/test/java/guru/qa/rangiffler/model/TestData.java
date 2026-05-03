package guru.qa.rangiffler.model;

import java.util.List;
import java.util.stream.Stream;

public record TestData(
    String password,
    List<UserJson> income,
    List<UserJson> outcome,
    List<UserJson> friends,
    List<UserJson> notFriends
) {

}
