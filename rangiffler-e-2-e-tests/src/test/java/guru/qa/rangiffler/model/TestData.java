package guru.qa.rangiffler.model;

import java.util.List;

public record TestData(
    String password,
    List<UserJson> income,
    List<UserJson> outcome,
    List<UserJson> friends
) {

}
