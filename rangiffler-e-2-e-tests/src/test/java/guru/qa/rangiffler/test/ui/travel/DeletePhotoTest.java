package guru.qa.rangiffler.test.ui.travel;

import static io.qameta.allure.SeverityLevel.BLOCKER;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.Photo;
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
@Feature("Profile")
@DisplayName(TestPrefix.UI + TestIcon.TRAVELS + "Travels")
@ParametersAreNonnullByDefault
public class DeletePhotoTest {

  @User(
      myPhotos = @Photo(
          country = Country.US,
          description = "my travel photo",
          img = TravelPhotoImage.NEW_YORK)
  )
  @Test
  @ApiLogin
  @DisplayName(TestPrefix.UI_POSITIVE + "[Only my travels] Удаление фото")
  void shouldBeIsDeletedMyTravelPhoto() {
    TravelsPage.open()
        .getPhotoGallery()
        .deletePhotoByDescription("my travel photo")
        .getTravelsPage()
        .checkSnackbarText("Post deleted")
        .refresh()
        .getPhotoGallery()
        .exactlyPhotoCardsInAnyOrder();
  }

  @User(
      myPhotos = @Photo(
          country = Country.US,
          description = "my travel photo",
          img = TravelPhotoImage.NEW_YORK)
  )
  @Test
  @ApiLogin
  @DisplayName(TestPrefix.UI_POSITIVE + "[With friends] Удаление фото")
  void shouldBeIsDeletedMyTravelPhotoFromWithFriends() {
    TravelsPage.open()
        .clickWithFriends()
        .getPhotoGallery()
        .deletePhotoByDescription("my travel photo")
        .getTravelsPage()
        .checkSnackbarText("Post deleted")
        .refresh()
        .clickWithFriends()
        .getPhotoGallery()
        .exactlyPhotoCardsInAnyOrder();
  }
}
