package guru.qa.rangiffler.util;

import java.nio.charset.StandardCharsets;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record ByteAsString(byte[] content) {

  public @Nonnull String string() {
    return content == null ? "" : new String(content, StandardCharsets.UTF_8);
  }
}
