package guru.qa.rangiffler.jupiter.extension;

import static guru.qa.rangiffler.helpers.AnnotationUtils.createdStore;
import static guru.qa.rangiffler.jupiter.extension.TestMethodContextExtension.context;

import guru.qa.rangiffler.helpers.AnnotationUtils;
import guru.qa.rangiffler.helpers.RandomDataUtils;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.PhotoCardJson;
import guru.qa.rangiffler.model.TestData;
import guru.qa.rangiffler.model.TestIcon;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.service.impl.UsersClient;
import io.qameta.allure.Step;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
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

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationUtils.findTestMethodAnnotation(User.class)
        .ifPresent(anno -> {
              if (anno.username().isEmpty()) {
                context.getStore(NAMESPACE).put(
                    context.getUniqueId(),
                    createTestUsers(anno)
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

  @Step(TestIcon.BEFORE + "Создание тестового окружения")
  private static UserJson createTestUsers(User userAnno) {
    final String username = RandomDataUtils.getRandomUserName();
    final UsersClient usersClient = UsersClient.create(username, userAnno.myPhotos());
    final UserJson user = usersClient.currentUser();

    final List<UserJson> income = userAnno.incomeInvitations() > 0
        ? usersClient.createIncomeInvitation(user, userAnno.incomeInvitations(), null)
        : List.of();
    final List<UserJson> outcome = userAnno.outcomeInvitations() > 0
        ? usersClient.createOutcomeInvitation(userAnno.outcomeInvitations())
        : List.of();
    final List<UserJson> friends = userAnno.friends() > 0
        ? usersClient.createFriends(user, userAnno.friends(), userAnno.friendsPhotos())
        : List.of();
    final List<UserJson> emptyPeople = IntStream.range(0, userAnno.emptyPeople())
        .mapToObj(u -> UsersClient.create(RandomDataUtils.getRandomUserName(), null))
        .map(UsersClient::currentUser)
        .toList();

    final List<PhotoCardJson> myPhotos = Stream.of(userAnno.myPhotos()).map(PhotoCardJson::fromPhotoAnno).toList();
    final List<PhotoCardJson> friendsPhotos = Stream.of(userAnno.friendsPhotos()).map(PhotoCardJson::fromPhotoAnno)
        .toList();

    final TestData testData = new TestData(
        UserExtension.DEFAULT_PASSWORD,
        income,
        outcome,
        friends,
        emptyPeople,
        myPhotos,
        friendsPhotos
    );
    return user.addTestData(testData);
  }
}
