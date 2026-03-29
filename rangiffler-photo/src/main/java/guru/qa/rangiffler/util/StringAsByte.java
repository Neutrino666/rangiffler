package guru.qa.rangiffler.util;

import java.nio.charset.StandardCharsets;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record StringAsByte(
    String content
) {
  public byte[] bytes() {
    return content.getBytes(StandardCharsets.UTF_8);
  }
}
