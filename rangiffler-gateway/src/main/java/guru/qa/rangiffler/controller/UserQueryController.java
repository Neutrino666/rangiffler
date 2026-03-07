package guru.qa.rangiffler.controller;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import rangiffler.graphqlTypes.Country;
import rangiffler.graphqlTypes.User;


@Controller
@PreAuthorize("isAuthenticated()")
public class UserQueryController {

  @QueryMapping
  public User user() {
    return User.newBuilder()
        .id("1ba62f37-a3da-4e4f-ac99-40d83daa099c")
        .username("barsik")
        .firstname("Kotik")
        .surname("Мяу")
        .location(
            Country.newBuilder()
                .code("ru")
                .name("Russian Federation")
                .build()
        )
        .build();
  }
}
