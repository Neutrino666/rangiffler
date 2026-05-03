package guru.qa.rangiffler.page;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.helpers.SelenideUtils;
import guru.qa.rangiffler.model.enums.Country;
import guru.qa.rangiffler.model.enums.Image;
import guru.qa.rangiffler.page.components.Header;
import io.qameta.allure.Step;
import java.awt.image.BufferedImage;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;

@ParametersAreNonnullByDefault
public final class ProfilePage extends BasePage<ProfilePage> {

  public static final @Nonnull String ICON = "\uD83E\uDDD1 ";
  public static final @Nonnull String URL = CFG.frontUrl() + "profile";

  private final SelenideElement self = $(".MuiContainer-maxWidthLg").as(ICON);
  private final SelenideElement title = self.$(".MuiTypography-h4").as("Title");
  private final SelenideElement avatarInput = self.$("#image__input").as("Avatar input");
  private final SelenideElement uploadedAvatar = self.$("img.MuiAvatar-img").as("Uploaded avatar");
  private final SelenideElement fallbackAvatar = self.$(".MuiAvatar-fallback").as("Avatar fallback");
  private final SelenideElement usernameInput = self.$("#username").as("Username input");
  private final SelenideElement firstnameInput = self.$("#firstname").as("Firstname input");
  private final SelenideElement surnameInput = self.$("#surname").as("Surname input");
  private final SelenideElement locationInput = self.$("#location").as("Location dropdown");
  private final SelenideElement reset = self.$("button[type='button']").as("Reset");
  private final SelenideElement save = self.$("button[type='submit']").as("Submit");
  private final SelenideElement surnameHelperText = self.$("#surname-helper-text").as("Surname helper text");
  private final SelenideElement firstnameHelperText = self.$("#firstname-helper-text").as("Surname helper text");

  private final ElementsCollection locations = $$("li[data-value]").as("Countries");

  @Getter
  private final Header header = new Header();

  @Step(ICON + "Открываем")
  public static @Nonnull ProfilePage open() {
    return Selenide.open(URL, ProfilePage.class);
  }

  @Step(ICON + "Проверка загрузки")
  public @Nonnull ProfilePage checkThatPageLoaded() {
    SelenideUtils.visible(self, title);
    avatarInput.shouldBe(exist);
    SelenideUtils.visibleAndInteractable(
        usernameInput, firstnameInput, surnameInput, reset, surnameInput, save,locationInput
    );
    header.checkThatComponentLoaded();
    return this;
  }

  @Step(ICON + "Check is present fallback avatar")
  public @Nonnull ProfilePage checkIsPresentFallbackAvatar() {
    fallbackAvatar.shouldBe(visible);
    return this;
  }

  @Step(ICON + "Set firstname")
  public @Nonnull ProfilePage setAvatar(Image photo) {
    avatarInput.uploadFromClasspath(photo.getDirResources());
    return this;
  }

  @Step(ICON + "Set firstname")
  public @Nonnull ProfilePage setFirstname(String firstname) {
    firstnameInput.setValue(firstname);
    return this;
  }

  @Step(ICON + "Set surname")
  public @Nonnull ProfilePage setSurname(String surname) {
    surnameInput.setValue(surname);
    return this;
  }

  @Step(ICON + "Set country")
  public @Nonnull ProfilePage setLocation(Country country) {
    locationInput.click();
    locations.findBy(attribute("data-value", country.getCode()))
        .click();
    return this;
  }

  @Step(ICON + "Check isPresent surname helper text")
  public @Nonnull ProfilePage checkIsPresentSurnameHelperText() {
    surnameHelperText.shouldBe(visible, text("Surname length has to be not longer that 100 symbols"));
    return this;
  }

  @Step(ICON + "Check isPresent surname helper text")
  public @Nonnull ProfilePage checkIsPresentFirstnameHelperText() {
    firstnameHelperText.shouldBe(visible, text("First name length has to be not longer that 50 symbols"));
    return this;
  }

  @Step(ICON + "Save")
  public @Nonnull ProfilePage save() {
    save.click();
    return this;
  }

  @Step(ICON + "check image diff")
  public @Nonnull ProfilePage assertAvatar(final BufferedImage expected) {
    assertScreen(expected, uploadedAvatar);
    return this;
  }
}
