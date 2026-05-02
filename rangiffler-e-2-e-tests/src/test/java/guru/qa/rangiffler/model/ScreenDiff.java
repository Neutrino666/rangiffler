package guru.qa.rangiffler.model;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record ScreenDiff(String expected,
                         String actual,
                         String diff) {

}
