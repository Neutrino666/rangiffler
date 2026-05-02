package guru.qa.rangiffler.jupiter.extension.allure;

import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.jupiter.extension.SuiteExtension;
import guru.qa.rangiffler.model.allure.AllureResults;
import guru.qa.rangiffler.model.allure.DecodedAllureFile;
import guru.qa.rangiffler.service.impl.AllureDockerApiClient;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;

@Slf4j
@ParametersAreNonnullByDefault
public final class AllureDockerExtension implements SuiteExtension {

  public static final boolean IN_DOCKER = "docker".equals(System.getProperty("test.env"));

  private static final Base64.Encoder encoder = Base64.getEncoder();
  private static final Path allureResultsDirectory = Path.of("./rangiffler-e-2-e-tests/build/allure-results");
  private static final String projectId = Config.projectId;

  private static final AllureDockerApiClient allureDockerApiClient = new AllureDockerApiClient();
  private boolean allureBroken = false;

  @Override
  public void beforeSuite(ExtensionContext context) {
    if (IN_DOCKER) {
      try {
        allureDockerApiClient.createProjectIfNotExist(projectId);
        allureDockerApiClient.clean(projectId);
      } catch (Throwable e) {
        log.error("### Error when create or clean project", e);
        allureBroken = true;
      }
    }
  }

  @Override
  public void afterSuite() {
    if (IN_DOCKER && !allureBroken) {
      try (Stream<Path> paths = Files.walk(allureResultsDirectory).filter(Files::isRegularFile)) {
        List<DecodedAllureFile> filesToSend = new ArrayList<>();
        for (Path allureResult : paths.toList()) {
          try (InputStream is = Files.newInputStream(allureResult)) {
            filesToSend.add(
                new DecodedAllureFile(
                    allureResult.getFileName().toString(),
                    encoder.encodeToString(is.readAllBytes())
                )
            );
          }
        }
        allureDockerApiClient.sendResultsToAllure(
            projectId,
            new AllureResults(
                filesToSend
            )
        );
        allureDockerApiClient.generateReport(
            projectId,
            System.getenv("HEAD_COMMIT_MESSAGE"),
            System.getenv("BUILD_URL"),
            System.getenv("EXECUTION_TYPE")
        );
      } catch (Throwable e) {
        log.error("### Error when create report", e);
      }
    }
  }
}
