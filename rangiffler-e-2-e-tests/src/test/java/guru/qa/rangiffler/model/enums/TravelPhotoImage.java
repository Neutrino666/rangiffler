package guru.qa.rangiffler.model.enums;

import java.util.stream.Stream;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;

@Getter
@ParametersAreNonnullByDefault
public enum TravelPhotoImage {

  ALMATY("almaty.jpg"),
  BAIKAL("baikal.jpg"),
  NEW_YORK("ny.jpg");

  private final String fileName;
  private final String dirResources;
  private final String extension;

  TravelPhotoImage(String value) {
    final String imgPrefix = "img/";
    this.fileName = value;
    this.dirResources = imgPrefix + value;
    String extension = Stream.of(this.fileName.split("\\."))
        .toList()
        .getLast();
    this.extension = "jpg".equals(extension) ? "jpeg" : extension;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "." + name();
  }
}
