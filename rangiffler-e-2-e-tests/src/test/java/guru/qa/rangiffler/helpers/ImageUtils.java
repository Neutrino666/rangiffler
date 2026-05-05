package guru.qa.rangiffler.helpers;

import guru.qa.rangiffler.model.enums.TravelPhotoImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ImageUtils {

  @Nonnull
  public static String base64(TravelPhotoImage img) {
    try {
      final URL resource = ImageUtils.class.getClassLoader().getResource(img.getDirResources());
      final File imgFile = Paths.get(Objects.requireNonNull(resource).toURI()).toFile();
      byte[] content = org.apache.commons.io.FileUtils.readFileToByteArray(imgFile);
      return "data:image/%s;base64,".formatted(img.getExtension()) + Base64.getEncoder().encodeToString(content);
    } catch (URISyntaxException | IOException e) {
      throw new RuntimeException("### Файл: %s е найден".formatted(img.getFileName()) + e);
    }
  }
}
