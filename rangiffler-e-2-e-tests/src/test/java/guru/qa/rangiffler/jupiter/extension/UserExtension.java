package guru.qa.rangiffler.jupiter.extension;

import static guru.qa.rangiffler.helpers.AnnotationUtils.createdStore;
import static guru.qa.rangiffler.jupiter.extension.TestMethodContextExtension.context;

import guru.qa.rangiffler.helpers.AnnotationUtils;
import guru.qa.rangiffler.helpers.RandomDataUtils;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.TestData;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.service.impl.AuthApiClient;
import guru.qa.rangiffler.service.impl.UsersClient;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

@ParametersAreNonnullByDefault
public final class UserExtension implements
    BeforeEachCallback,
    ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(
      UserExtension.class);
  public static final String DEFAULT_PASSWORD = "12345";
  private final AuthApiClient authApiClient = new AuthApiClient();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationUtils.findTestMethodAnnotation(User.class)
        .ifPresent(
            userAnno -> {
              if (userAnno.username().isEmpty()) {
                final String username = RandomDataUtils.getRandomUserName();
                authApiClient.register(username, DEFAULT_PASSWORD);
                final String token = authApiClient.login(username, DEFAULT_PASSWORD);
                final UsersClient usersClient = new UsersClient(token);
                final UserJson user = usersClient.currentUser();

                final List<UserJson> income = userAnno.incomeInvitations() > 0
                    ? usersClient.createIncomeInvitation(user, userAnno.incomeInvitations())
                    : List.of();
                final List<UserJson> outcome = userAnno.outcomeInvitations() > 0
                    ? usersClient.createOutcomeInvitation(userAnno.outcomeInvitations())
                    : List.of();
                final List<UserJson> friends = userAnno.friends() > 0
                    ? usersClient.createFriends(user, userAnno.friends())
                    : List.of();
                final List<UserJson> emptyPeople = IntStream.range(0, userAnno.emptyPeople())
                    .mapToObj(u -> UsersClient.create(RandomDataUtils.getRandomUserName()))
                    .map(UsersClient::currentUser)
                    .toList();

                final TestData testData = new TestData(
                    DEFAULT_PASSWORD,
                    income,
                    outcome,
                    friends,
                    emptyPeople
                );

                context.getStore(NAMESPACE).put(
                    context.getUniqueId(),
                    user.addTestData(testData)
                );
              }
            }
        );
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
  }

  @Override
  @Nonnull
  public UserJson resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return Objects.requireNonNull(
        createdStore(NAMESPACE, UserJson.class)
    );
  }

  @Nonnull
  public static Optional<UserJson> createdUser() {
    final ExtensionContext methodContext = context();
    return Optional.ofNullable(methodContext.getStore(NAMESPACE)
        .get(methodContext.getUniqueId(), UserJson.class));
  }

  public static void setUser(UserJson user) {
    context().getStore(NAMESPACE).put(
        context().getUniqueId(),
        user
    );
  }
}
