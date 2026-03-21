package guru.qa.rangiffler.util;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum ImageFormat {
  PNG("png"),
  JPEG("jpeg");

  @Getter(AccessLevel.NONE)
  private static final Map<String, ImageFormat> imageFormats;
  private final String format;

  static {
    imageFormats = Stream.of(ImageFormat.values())
        .collect(Collectors.toMap(ImageFormat::getFormat, imageFormat -> imageFormat));
  }

  public static ImageFormat fromBase64(String base64) {
    String searchFormat = StringUtils.substringBetween(base64, "data:image/", ";base64,");
    return Optional.ofNullable(imageFormats.get(searchFormat))
        .orElseThrow(() -> {
          String message = "### Error while image extension determine";
          log.error(message);
          return new RuntimeException(message);
        });
  }
}
