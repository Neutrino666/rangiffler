package guru.qa.rangiffler.test.ui.travel;

import static io.qameta.allure.SeverityLevel.BLOCKER;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.Photo;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.meta.WebTest;
import guru.qa.rangiffler.model.PhotoJson;
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
public class ViewPhotoTest {

  @User
  @Test
  @ApiLogin
  @DisplayName(TestPrefix.UI_POSITIVE + "[Only my travels] Просмотр моих путешествий без фото")
  void shouldBeEmptyMyTravelsPhoto() {
    TravelsPage.open()
        .getPhotoGallery()
        .exactlyPhotoCardsInAnyOrder();
  }

  @User
  @Test
  @ApiLogin
  @DisplayName(TestPrefix.UI_POSITIVE + "[With friends] Просмотр путешествий друзей без фото")
  void shouldBeEmptyFriendsTravelPhoto() {
    TravelsPage.open()
        .clickWithFriends()
        .checkThatPageLoaded()
        .getPhotoGallery()
        .exactlyPhotoCardsInAnyOrder();
  }

  @User(
      myPhotos = @Photo(
          img = TravelPhotoImage.BAIKAL,
          country = Country.RU,
          description = "my photo")
  )
  @Test
  @ApiLogin
  @DisplayName(TestPrefix.UI_POSITIVE + "[With friends] Просмотр путешествий с фото: мои")
  void shouldBeExistMyTravelsPhoto(UserJson user) {
    final PhotoJson myPhoto = user.getTestData().myPhotos().getFirst();
    TravelsPage.open()
        .clickWithFriends()
        .getPhotoGallery()
        .exactlyPhotoCardsInAnyOrder(myPhoto);
  }

  @User(
      myPhotos = @Photo(
          img = TravelPhotoImage.BAIKAL,
          country = Country.RU,
          description = "my photo"),
      hasLikeMyPhoto = true,
      friends = 1,
      friendsPhotos = @Photo(
          img = TravelPhotoImage.ALMATY,
          country = Country.KZ,
          description = "friend photo")
  )
  @Test
  @ApiLogin
  @DisplayName(TestPrefix.UI_POSITIVE + "[With friends] Просмотр путешествий с фото и лайком")
  void shouldBeExistMyAndFriendTravelsPhotoWithLikes(UserJson user) {
    final PhotoJson myPhoto = user.getTestData().myPhotos().getFirst();
    final PhotoJson friendPhoto = user.getTestData().friendPhotos().getFirst();
    TravelsPage.open()
        .clickWithFriends()
        .getPhotoGallery()
        .exactlyPhotoCardsInAnyOrder(myPhoto, friendPhoto);
  }
}
