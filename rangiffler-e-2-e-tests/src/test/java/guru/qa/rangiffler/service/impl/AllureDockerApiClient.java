package guru.qa.rangiffler.service.impl;

import guru.qa.rangiffler.api.AllureDockerApi;
import guru.qa.rangiffler.model.allure.AllureProject;
import guru.qa.rangiffler.model.allure.AllureResults;
import java.io.IOException;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.extern.slf4j.Slf4j;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.Assertions;

@Slf4j
@ParametersAreNonnullByDefault
public final class AllureDockerApiClient extends RestClient {

  private final AllureDockerApi allureDockerApi;

  public AllureDockerApiClient() {
    super(CFG.allureDockerUrl(), HttpLoggingInterceptor.Level.NONE);
    this.allureDockerApi = create(AllureDockerApi.class);
  }

  public void clean(String projectId) throws IOException {
    allureDockerApi.cleanResults(projectId).execute();
  }

  public void generateReport(String projectId,
      String executionName,
      String executionFrom,
      String executionType) throws IOException {
    allureDockerApi.generateReport(projectId, executionName, executionFrom, executionType).execute();
  }

  public void sendResultsToAllure(String projectId, AllureResults allureResults) throws IOException {
    int code = allureDockerApi.uploadResults(
        projectId,
        allureResults
    ).execute().code();
    Assertions.assertEquals(HttpStatus.SC_OK, code);
  }

  public void createProjectIfNotExist(String projectId) throws IOException {
    int code = allureDockerApi.project(
        projectId
    ).execute().code();
    if (code == HttpStatus.SC_NOT_FOUND) {
      code = allureDockerApi.createProject(new AllureProject(projectId)).execute().code();
      Assertions.assertEquals(HttpStatus.SC_CREATED, code);
    } else {
      Assertions.assertEquals(HttpStatus.SC_OK, code);
    }
  }
}
