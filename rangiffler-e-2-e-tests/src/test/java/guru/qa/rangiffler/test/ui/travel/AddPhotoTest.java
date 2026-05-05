package guru.qa.rangiffler.test.ui.travel;

import static io.qameta.allure.SeverityLevel.BLOCKER;

import guru.qa.rangiffler.helpers.RandomDataUtils;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.meta.WebTest;
import guru.qa.rangiffler.model.TestIcon;
import guru.qa.rangiffler.model.TestPrefix;
import guru.qa.rangiffler.model.enums.Country;
import guru.qa.rangiffler.model.enums.TravelPhotoImage;
import guru.qa.rangiffler.page.TravelsPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@WebTest
@Tag("smoke")
@Severity(BLOCKER)
@Feature("Путешествия")
@DisplayName(TestPrefix.UI + TestIcon.TRAVELS + "Travels")
@ParametersAreNonnullByDefault
public class AddPhotoTest {

  @User
  @Test
  @ApiLogin
  @DisplayName(TestPrefix.UI_POSITIVE + "Добавление путешествия с фото")
  void shouldBeCreatedTravelPhoto() {
    final String description = RandomDataUtils.getRandomTravelDescription();
    TravelsPage.open()
        .addPhoto(TravelPhotoImage.NEW_YORK, Country.US, description)
        .checkSnackbarText("New post created");
  }

  @User
  @Test
  @ApiLogin
  @DisplayName(TestPrefix.UI_NEGATIVE + "Добавление путешествия без фото")
  void shouldBeErrorWhenCreateWithoutPhoto() {
    final String description = RandomDataUtils.getRandomTravelDescription();
    TravelsPage.open()
        .clickAddPhoto()
        .setDescription(description)
        .save()
        .checkErrorMessage("Please upload an image");
  }
}
