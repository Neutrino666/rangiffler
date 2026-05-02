package guru.qa.rangiffler.test.ui.profile;

import static io.qameta.allure.SeverityLevel.BLOCKER;

import guru.qa.rangiffler.helpers.RandomDataUtils;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.ScreenShotTest;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.meta.WebTest;
import guru.qa.rangiffler.model.Country;
import guru.qa.rangiffler.model.Image;
import guru.qa.rangiffler.model.TestPrefix;
import guru.qa.rangiffler.page.ProfilePage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import java.awt.image.BufferedImage;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@WebTest
@Tag("smoke")
@Severity(BLOCKER)
@Feature("Profile edit")
@DisplayName(TestPrefix.UI + "Profile " + ProfilePage.ICON)
@ParametersAreNonnullByDefault
public class ProfileUpdateTest {

  @User
  @Test
  @ApiLogin
  @DisplayName(TestPrefix.UI_POSITIVE + "Редактирование профиля")
  void updateProfileShouldBeSuccess() {
    ProfilePage.open()
        .checkThatPageLoaded()
        .setLocation(Country.KZ)
        .setFirstname("Cyber")
        .setSurname("Duck")
        .save()
        .checkSnackbarText("Your profile is successfully updated");
  }

  @User
  @ApiLogin
  @ScreenShotTest
  @DisplayName(TestPrefix.UI_POSITIVE + "Загрузка аватара")
  void uploadedAvatarIsNotHaveDifference(BufferedImage expected) {
    ProfilePage.open()
        .checkThatPageLoaded()
        .setAvatar(Image.CYBER_DUCK)
        .save()
        .assertAvatar(expected);
  }

  @User
  @Test
  @ApiLogin
  @DisplayName(TestPrefix.UI_NEGATIVE + "Редактирование Firstname - более 50")
  void shouldShowErrorThatTooLongFirstname() {
    ProfilePage.open()
        .setFirstname(RandomDataUtils.getRandomPassword(51, 52))
        .save()
        .checkIsPresentFirstnameHelperText();
  }

  @User
  @Test
  @ApiLogin
  @DisplayName(TestPrefix.UI_NEGATIVE + "Редактирование Surname - более 100")
  void shouldShowErrorThatTooLongSurname() {
    ProfilePage.open()
        .setSurname(RandomDataUtils.getRandomPassword(101, 102))
        .save()
        .checkIsPresentSurnameHelperText();
  }
}
