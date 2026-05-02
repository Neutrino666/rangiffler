package guru.qa.rangiffler.page;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.helpers.SelenideUtils;
import guru.qa.rangiffler.page.components.Header;
import io.qameta.allure.Step;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;

@ParametersAreNonnullByDefault
public final class ProfilePage extends BasePage<ProfilePage> {

  public static final String ICON = "\uD83D\uDC64 ";
  public static final String URL = CFG.frontUrl() + "profile";

  private final SelenideElement self = $(".MuiContainer-maxWidthLg").as(ICON);
  private final SelenideElement title = self.$(".MuiTypography-h4").as("Title");
  private final SelenideElement avatarInput = self.$("#image__input").as("Avatar input");
  private final SelenideElement uploadedAvatar = self.$("img.MuiAvatar-img").as("Uploaded avatar");
  private final SelenideElement fallbackAvatar = self.$(".MuiAvatar-fallback").as("Avatar fallback");
  private final SelenideElement usernameInput = self.$("#username").as("Username input");
  private final SelenideElement firstnameInput = self.$("#firstname").as("Firstname input");
  private final SelenideElement surnameInput = self.$("#surname").as("Surname input");
  private final SelenideElement locationSelect = self.$("#select-location").as("Select location");
  private final SelenideElement reset = self.$("button[type='button']").as("Reset");
  private final SelenideElement save = self.$("button[type='submit']").as("Submit");

  @Getter
  private final Header header = new Header();

  @Step(ICON + "Открываем")
  public static @Nonnull ProfilePage open() {
    return Selenide.open(URL, ProfilePage.class);
  }

  @Step(ICON + "Проверка загрузки")
  public @Nonnull ProfilePage checkThatPageLoaded() {
    SelenideUtils.visible(self, title);
    SelenideUtils.exist(locationSelect, avatarInput);
    SelenideUtils.visibleAndInteractable(
        usernameInput, firstnameInput, surnameInput, reset, surnameInput, save
    );
    header.checkThatComponentLoaded();
    return this;
  }
}
