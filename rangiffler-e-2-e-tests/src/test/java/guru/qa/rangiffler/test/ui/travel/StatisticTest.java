package guru.qa.rangiffler.test.ui.travel;

import static io.qameta.allure.SeverityLevel.NORMAL;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.Photo;
import guru.qa.rangiffler.jupiter.annotation.ScreenShotTest;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.meta.WebTest;
import guru.qa.rangiffler.model.TestIcon;
import guru.qa.rangiffler.model.TestPrefix;
import guru.qa.rangiffler.model.enums.Country;
import guru.qa.rangiffler.model.enums.Screensont;
import guru.qa.rangiffler.model.enums.TravelPhotoImage;
import guru.qa.rangiffler.page.TravelsPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import java.awt.image.BufferedImage;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

@WebTest
@Tag("smoke")
@Severity(NORMAL)
@Feature("Статистика")
@DisplayName(TestPrefix.UI + TestIcon.TRAVELS + "Travels")
@ParametersAreNonnullByDefault
public class StatisticTest {

  @User(
      myPhotos = {
          @Photo(
              img = TravelPhotoImage.ALMATY,
              country = Country.KZ,
              description = "my Almaty photo 1"),
          @Photo(
              img = TravelPhotoImage.ALMATY,
              country = Country.KZ,
              description = "my Almaty photo 2")
      },
      friends = 1,
      friendsPhotos = @Photo(
          img = TravelPhotoImage.BAIKAL,
          country = Country.RU,
          description = "my Russia photo")
  )
  @ApiLogin
  @ScreenShotTest(value = Screensont.TRAVEL_MAP_KZ2_RU1)
  @DisplayName(TestPrefix.UI_POSITIVE + "[With friends] Градация путешествий на карте")
  void shouldBeExistMyTravelsPhotoWithFriends(BufferedImage expected) {
    TravelsPage.open()
        .clickWithFriends()
        .assertScreen(expected);
  }

  @User
  @ApiLogin
  @ScreenShotTest(value = Screensont.TRAVEL_MAP_EMPTY)
  @DisplayName(TestPrefix.UI_POSITIVE + "[Only my travels] Карта без путешествий")
  void shouldBeNotExistMyTravelsPhoto(BufferedImage expected) {
    TravelsPage.open()
        .assertScreen(expected);
  }

  @User
  @ApiLogin
  @ScreenShotTest(value = Screensont.TRAVEL_MAP_EMPTY)
  @DisplayName(TestPrefix.UI_POSITIVE + "[With friends] Карта без путешествий")
  void shouldBeNotExistTravelsPhotoWhenWithFriends(BufferedImage expected) {
    TravelsPage.open()
        .assertScreen(expected);
  }
}
