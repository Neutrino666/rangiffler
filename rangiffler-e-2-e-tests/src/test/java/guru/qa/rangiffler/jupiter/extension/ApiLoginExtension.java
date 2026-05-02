package guru.qa.rangiffler.jupiter.extension;

import static guru.qa.rangiffler.jupiter.extension.TestMethodContextExtension.context;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.rangiffler.api.rest.core.ThreadSafeCookieStore;
import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.helpers.AnnotationUtils;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.Token;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.TestData;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.service.impl.AuthApiClient;
import guru.qa.rangiffler.service.impl.UsersGraphQLClient;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;

@ParametersAreNonnullByDefault
public final class ApiLoginExtension implements
    BeforeEachCallback,
    ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(
      ApiLoginExtension.class);
  private final Config CFG = Config.getInstance();

  private final AuthApiClient authApiClient = new AuthApiClient();
  private final boolean setupBrowser;

  private ApiLoginExtension(boolean setupBrowser) {
    this.setupBrowser = setupBrowser;
  }

  private ApiLoginExtension() {
    this.setupBrowser = true;
  }

  public static ApiLoginExtension rest() {
    return new ApiLoginExtension(false);
  }

  @Override
  public void beforeEach(final ExtensionContext context) throws Exception {
    AnnotationUtils.findTestMethodAnnotation(ApiLogin.class)
        .ifPresent(
            apiLogin -> {
              final UserJson fakeUser = findUser(apiLogin);
              ThreadSafeCookieStore.INSTANCE.removeAll();
              final String token = authApiClient.login(
                  fakeUser.getUsername(),
                  fakeUser.getTestData().password()
              );
              setToken(token);
              if (setupBrowser) {
                openMainPageInSelenide(token);
              }
            }
        );
  }

  @Override
  public boolean supportsParameter(final ParameterContext parameterContext,
      final ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(String.class)
        && AnnotationSupport.isAnnotated(parameterContext.getParameter(), Token.class);
  }

  @Override
  public String resolveParameter(final ParameterContext parameterContext,
      final ExtensionContext extensionContext) throws ParameterResolutionException {
    return "Bearer " + getToken();
  }

  public static void setToken(final String token) {
    context().getStore(NAMESPACE).put("token", token);
  }

  public static String getToken() {
    return context().getStore(NAMESPACE).get("token", String.class);
  }

  public static void setCode(final String code) {
    context().getStore(NAMESPACE).put("code", code);
  }

  public static String getCode() {
    return context().getStore(NAMESPACE).get("code", String.class);
  }

  @Nonnull
  public static Cookie getJsessionIdCookie() {
    return new Cookie(
        "JSESSIONID",
        ThreadSafeCookieStore.INSTANCE.value("JSESSIONID")
    );
  }

  private UserJson findUser(ApiLogin apiLogin) {
    final UserJson fakeUser;
    final Optional<UserJson> userFromUserExt = UserExtension.createdUser();
    if ("".equals(apiLogin.username()) || "".equals(apiLogin.password())) {
      String errorMsg = "@%s должна присутствовать, когда не заполнена аннотация @%s!"
          .formatted(User.class.getSimpleName(), ApiLogin.class.getSimpleName());
      return userFromUserExt.orElseThrow(() -> new IllegalStateException(errorMsg));
    } else {
      if (userFromUserExt.isPresent()) {
        String errorMsg = "@%s должна отсутствовать, так как заполнена аннотация @%s!"
            .formatted(User.class.getSimpleName(), ApiLogin.class.getSimpleName());
        throw new IllegalArgumentException(errorMsg);
      }
      fakeUser = new UserJson();
      fakeUser.setUsername(apiLogin.username());
      fakeUser.addTestData(collectTestData(apiLogin.username(), apiLogin.password()));
      UserExtension.setUser(fakeUser);
      return new UserJson();
    }
  }

  private TestData collectTestData(String username, String password) {
    if (username.isEmpty() && password.isEmpty()) {
      throw new RuntimeException("Пароль и логин должны быть заданы");
    }
    UsersGraphQLClient userClient = new UsersGraphQLClient(authApiClient.login(username, password));
    final List<UserJson> income = userClient.invitationsQuery(0, Integer.MAX_VALUE, "");
    final List<UserJson> outcome = userClient.outcomeInvitationsQuery(0, Integer.MAX_VALUE, "");
    final List<UserJson> friends = userClient.friendsQuery(0, Integer.MAX_VALUE, "");

    return new TestData(
        password,
        income,
        outcome,
        friends
    );
  }

  private void openMainPageInSelenide(String token) {
    Selenide.open(CFG.frontUrl());
    Selenide.localStorage().setItem("id_token", token);
    WebDriverRunner.getWebDriver().manage().addCookie(
        new Cookie(
            "JSESSIONID",
            ThreadSafeCookieStore.INSTANCE.value("JSESSIONID")
        )
    );
  }
}
