package guru.qa.rangiffler.service.impl;

import static guru.qa.rangiffler.helpers.OAuth2Utils.generateCodeChallenge;
import static guru.qa.rangiffler.helpers.OAuth2Utils.generateCodeVerifier;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.rangiffler.api.rest.core.CodeInterceptor;
import guru.qa.rangiffler.api.rest.user.AuthApi;
import guru.qa.rangiffler.api.rest.user.OAuth2Api;
import guru.qa.rangiffler.jupiter.extension.ApiLoginExtension;
import io.qameta.allure.Step;
import java.io.IOException;
import java.net.CookieStore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import okhttp3.ResponseBody;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Call;
import retrofit2.Response;

@ParametersAreNonnullByDefault
public final class AuthApiClient extends RestClient implements AuthApi {

  private final static String RESPONSE_TYPE = "code";
  private final static String CLIENT_ID = "client";
  private final static String SCOPE = "openid";
  private final static String REDIRECT_URI = CFG.frontUrl() + "authorized";
  private final static String GRANT_TYPE = "authorization_code";
  private final static String SHA = "S256";
  private final static String tokenLengthPattern = "([\\w\\d._-]{90,})";

  private final OAuth2Api oAuth2Api;

  public AuthApiClient(@Nullable CookieStore store) {
    super(CFG.authUrl(), true, store, new CodeInterceptor());
    oAuth2Api = create(OAuth2Api.class);
  }

  public AuthApiClient() {
    super(CFG.authUrl(), true, new CodeInterceptor());
    oAuth2Api = create(OAuth2Api.class);
  }

  @Nonnull
  public Response<Void> register(String username, String password) {
    return register(username, password, HttpStatus.SC_CREATED);
  }

  @Nonnull
  public Response<Void> register(String username, String password, int statusCode) {
    Response<ResponseBody> authorizeResponse = execute(this::requestRegisterForm);
    return execute(() -> register(
            username,
            password,
            password,
            findCsrf(authorizeResponse)
        ),
        statusCode
    );
  }

  @Nonnull
  @Step("[REST API] login OAuth2")
  public String login(String username, String password) {
    String codeVerifier = generateCodeVerifier();
    String codeChallenge = generateCodeChallenge(codeVerifier);
    Response<ResponseBody> authorizeResponse = execute(
        () -> authorize(
            RESPONSE_TYPE,
            CLIENT_ID,
            SCOPE,
            REDIRECT_URI,
            codeChallenge,
            SHA)
    );
    execute(
        () -> login(
            username,
            password,
            findCsrf(authorizeResponse)
        )
    );
    Response<JsonNode> tokenResponse = execute(
        () -> token(
            ApiLoginExtension.getCode(),
            REDIRECT_URI,
            codeVerifier,
            GRANT_TYPE,
            CLIENT_ID
        )
    );
    return tokenResponse.body().get("id_token").asText();
  }

  @Nonnull
  @Override
  @Step("[REST API] Получаем форму регистрации")
  public Call<ResponseBody> requestRegisterForm() {
    return oAuth2Api.requestRegisterForm();
  }

  @Nonnull
  @Override
  @Step("[REST API] Регистрация нового пользователя")
  public Call<Void> register(String username, String password, String passwordSubmit, String csrf) {
    return oAuth2Api.register(username, password, password, csrf);
  }

  @Nonnull
  @Override
  @Step("[REST API] authorize")
  public Call<ResponseBody> authorize(
      String responseType, String clientId, String scope, String redirectUri,
      String codeChallenge, String codeChallengeMethod
  ) {
    return oAuth2Api.authorize(responseType, clientId, scope, redirectUri, codeChallenge, codeChallengeMethod);
  }

  @Nonnull
  @Override
  @Step("[REST API] Логинимся")
  public Call<Void> login(String username, String password, String csrf) {
    return oAuth2Api.login(
        username,
        password,
        csrf
    );
  }

  @Nonnull
  @Override
  @Step("[REST API] Получаем токен")
  public Call<JsonNode> token(String code, String redirectUri, String codeVerifier, String grantType, String clientId) {
    return oAuth2Api.token(
        code,
        redirectUri,
        codeVerifier,
        grantType,
        clientId
    );
  }

  @Nonnull
  private String findCsrf(Response<ResponseBody> authorizeResponse) {
    if (authorizeResponse.isSuccessful() && authorizeResponse.body() != null) {
      Pattern pattern = Pattern.compile("_csrf\".+?value.+?\"%s?\"\\/>".formatted(tokenLengthPattern));
      Matcher matcherOld = null;
      try {
        matcherOld = pattern.matcher(authorizeResponse.body().string());
      } catch (IOException e) {
        throw new AssertionError(e);
      }
      if (matcherOld.find()) {
        return matcherOld.group(1);
      }
    }
    throw new IllegalStateException("not found _csrf");
  }
}
