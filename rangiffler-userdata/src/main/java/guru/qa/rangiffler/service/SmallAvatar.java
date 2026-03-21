package guru.qa.rangiffler.service;

import guru.qa.rangiffler.util.ImageFormat;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

@Slf4j
@ParametersAreNonnullByDefault
public class SmallAvatar {

  private final int height;
  private final int width;
  private final double quality;
  private final String outputFormat;
  private final String photo;

  public SmallAvatar(int height, int width, @Nullable String photo) {
    this(height, width, 1.0,  photo);
  }

  public SmallAvatar(int height, int width, double quality, @Nullable String photo) {
    this.height = height;
    this.width = width;
    this.quality = quality;
    this.outputFormat = ImageFormat.fromBase64(photo).getFormat();
    this.photo = photo;
  }

  public @Nullable byte[] bytes() {
    if (photo != null) {
      try {
        String base64Image = photo.split(",")[1];

        try (ByteArrayInputStream is = new ByteArrayInputStream(Base64.getDecoder().decode(base64Image));
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {

          Thumbnails.of(ImageIO.read(is))
              .height(height)
              .width(width)
              .outputQuality(quality)
              .outputFormat(outputFormat)
              .toOutputStream(os);

          return concatArrays(
              "data:image/png;base64,".getBytes(StandardCharsets.UTF_8),
              Base64.getEncoder().encode(os.toByteArray())
          );
        }
      } catch (Exception e) {
        log.error("### Error while resizing photo");
        throw new RuntimeException(e);
      }
    }
    return null;
  }

  private @Nonnull byte[] concatArrays(byte[] first, byte[] second) {
    byte[] result = Arrays.copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }
}
