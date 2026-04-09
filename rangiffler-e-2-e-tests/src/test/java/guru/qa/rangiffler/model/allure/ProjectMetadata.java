package guru.qa.rangiffler.model.allure;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record ProjectMetadata(@JsonProperty("message") String message) {

}
