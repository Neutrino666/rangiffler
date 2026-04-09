package guru.qa.rangiffler.model.allure;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record Projects(
    @JsonProperty("projects") Map<String, Map<String, String>> projects
) {

}
