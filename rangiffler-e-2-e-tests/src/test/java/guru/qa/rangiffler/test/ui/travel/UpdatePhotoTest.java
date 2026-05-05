package guru.qa.rangiffler.test.ui.travel;

import static io.qameta.allure.SeverityLevel.BLOCKER;

import guru.qa.rangiffler.helpers.RandomDataUtils;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.Photo;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.meta.WebTest;
import guru.qa.rangiffler.model.PhotoCardJson;
import guru.qa.rangiffler.model.TestIcon;
import guru.qa.rangiffler.model.TestPrefix;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.model.enums.Country;
import guru.qa.rangiffler.model.enums.TravelPhotoImage;
import guru.qa.rangiffler.page.TravelsPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@WebTest
@Tag("smoke")
@Severity(BLOCKER)
@Feature("Путешествия")
@DisplayName(TestPrefix.UI + TestIcon.TRAVELS + "Travels")
public class UpdatePhotoTest {

  @User(
      myPhotos = @Photo(
          img = TravelPhotoImage.BAIKAL,
          country = Country.RU,
          description = "my photo")
  )
  @Test
  @ApiLogin
  @DisplayName(TestPrefix.UI_POSITIVE + "[Only my travels] Просмотр формы редактирования фото: мои")
  void shouldBeExistMyTravelsPhotoEditForm(UserJson user) {
    final PhotoCardJson myPhoto = user.getTestData().myPhotos().getFirst();
    TravelsPage.open()
        .getPhotoGallery()
        .findPhotoCardByText(myPhoto.description())
        .clickEdit()
        .checkThatPageLoaded();
  }

  @User(
      myPhotos = @Photo(
          img = TravelPhotoImage.BAIKAL,
          country = Country.RU,
          description = "my photo ")
  )
  @Test
  @ApiLogin
  @DisplayName(TestPrefix.UI_POSITIVE + "[Only my travels] Редактирование фото")
  void shouldBeEditedMyTravelsPhoto(UserJson user) {
    final PhotoCardJson myPhoto = user.getTestData().myPhotos().getFirst();
    final PhotoCardJson update = new PhotoCardJson(
        TravelPhotoImage.ALMATY,
        myPhoto.likes(),
        Country.KZ,
        RandomDataUtils.getRandomTravelDescription()
    );
    TravelsPage.open()
        .getPhotoGallery()
        .findPhotoCardByText(myPhoto.description())
        .clickEdit()
        .update(update)
        .checkSnackbarText("Post updated")
        .refresh()
        .getPhotoGallery()
        .exactlyPhotoCardsInAnyOrder(update);
  }

  @User(
      myPhotos = @Photo(
          img = TravelPhotoImage.BAIKAL,
          country = Country.RU,
          description = "my photo ")
  )
  @Test
  @ApiLogin
  @DisplayName(TestPrefix.UI_POSITIVE + "[With friends] Редактирование фото")
  void shouldBeEditedMyTravelsPhotoFronWithFriends(UserJson user) {
    final PhotoCardJson myPhoto = user.getTestData().myPhotos().getFirst();
    final PhotoCardJson update = new PhotoCardJson(
        TravelPhotoImage.ALMATY,
        myPhoto.likes(),
        Country.KZ,
        RandomDataUtils.getRandomTravelDescription()
    );
    TravelsPage.open()
        .clickWithFriends()
        .getPhotoGallery()
        .findPhotoCardByText(myPhoto.description())
        .clickEdit()
        .update(update)
        .checkSnackbarText("Post updated")
        .refresh()
        .clickWithFriends()
        .getPhotoGallery()
        .exactlyPhotoCardsInAnyOrder(update);
  }
}
