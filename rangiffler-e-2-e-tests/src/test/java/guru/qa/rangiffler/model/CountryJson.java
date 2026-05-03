package guru.qa.rangiffler.model;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record CountryJson(
    String code,
    String name,
    @Nullable String flag
) {

}
