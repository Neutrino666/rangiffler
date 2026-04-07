package guru.qa.rangiffler.model.allure;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record ProjectResponse(
    @JsonProperty("data") Projects data,
    @JsonProperty("meta_data") ProjectMetadata metadata
) {

}
