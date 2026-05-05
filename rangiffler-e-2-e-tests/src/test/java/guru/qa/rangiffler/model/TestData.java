package guru.qa.rangiffler.model;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record TestData(
    String password,
    List<UserJson> income,
    List<UserJson> outcome,
    List<UserJson> friends,
    List<UserJson> notFriends,
    List<PhotoJson> myPhotos,
    List<PhotoJson> friendPhotos
) {

}
