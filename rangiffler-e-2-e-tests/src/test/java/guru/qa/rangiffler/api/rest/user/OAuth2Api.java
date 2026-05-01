package guru.qa.rangiffler.api.rest.user;

import com.fasterxml.jackson.databind.JsonNode;
import javax.annotation.Nonnull;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface OAuth2Api {

  @GET("register")
  @Nonnull
  Call<ResponseBody> requestRegisterForm();

  @POST("register")
  @FormUrlEncoded
  @Nonnull
  Call<Void> register(
      @Field("username") String username,
      @Field("password") String password,
      @Field("passwordSubmit") String passwordSubmit,
      @Field("_csrf") String csrf);

  @GET("oauth2/authorize")
  @Nonnull
  Call<ResponseBody> authorize(
      @Query("response_type") String responseType,
      @Query("client_id") String clientId,
      @Query("scope") String scope,
      @Query(value = "redirect_uri", encoded = true) String redirectUri,
      @Query("code_challenge") String codeChallenge,
      @Query("code_challenge_method") String codeChallengeMethod
  );

  @POST("login")
  @FormUrlEncoded
  @Nonnull
  Call<Void> login(
      @Field("username") String username,
      @Field("password") String password,
      @Field("_csrf") String csrf);

  @POST("oauth2/token")
  @FormUrlEncoded
  @Nonnull
  Call<JsonNode> token(
      @Field("code") String code,
      @Field(value = "redirect_uri", encoded = true) String redirectUri,
      @Field("code_verifier") String codeVerifier,
      @Field("grant_type") String grantType,
      @Field("client_id") String clientId
  );
}
