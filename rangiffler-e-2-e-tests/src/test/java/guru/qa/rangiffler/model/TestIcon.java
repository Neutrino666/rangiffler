package guru.qa.rangiffler.model;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface TestIcon {

  @Nonnull
  String PEOPLE = "\uD83D\uDC64 \uD83D\uDC64 \uD83D\uDC64 ";

  @Nonnull
  String PROFILE = "\uD83E\uDDD1 ";

  @Nonnull
  String TRAVELS = "\uD83C\uDF0E ";
  @Nonnull
  String PHOTO = "\uD83D\uDDBC\uFE0F ";
  @Nonnull
  String TRAVEL_PHOTO = TRAVELS + PHOTO;

  @Nonnull
  String WELCOME = "\uD83E\uDD17 ";
  @Nonnull
  String LOGIN = "\uD83D\uDD12 ";
  @Nonnull
  String REGISTRATION = "\uD83D\uDD11 ";

  @Nonnull
  String SNACKBAR = "\uD83D\uDD14 ";

  @Nonnull
  String BEFORE = "⚙\uFE0F ";

  @Nonnull
  String AFTER = "\uD83E\uDDF9 ";
}
