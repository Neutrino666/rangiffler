package guru.qa.rangiffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.rangiffler.model.allure.AllureProject;
import guru.qa.rangiffler.model.allure.AllureResults;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

@ParametersAreNonnullByDefault
public interface AllureDockerApi {

  @POST("allure-docker-service/send-results")
  @Nonnull
  Call<Void> uploadResults(
      @Query("project_id") String projectId,
      @Body AllureResults results);

  @GET("allure-docker-service/projects/{project_id}")
  @Nonnull
  Call<JsonNode> project(@Path("project_id") String projectId);

  @GET("allure-docker-service/clean-results")
  @Nonnull
  Call<Void> cleanResults(@Query("project_id") String projectId);

  @GET("allure-docker-service/generate-report")
  @Nonnull
  Call<Void> generateReport(
      @Query("project_id") String projectId,
      @Query("execution_name") String executionName,
      @Query(value = "execution_from", encoded = true) String executionFrom,
      @Query("execution_type") String executionType);

  @POST("allure-docker-service/projects")
  @Nonnull
  Call<Void> createProject(@Body AllureProject project);
}
