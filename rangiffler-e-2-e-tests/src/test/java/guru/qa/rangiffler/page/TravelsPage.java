package guru.qa.rangiffler.page;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.helpers.SelenideUtils;
import guru.qa.rangiffler.model.TestIcon;
import guru.qa.rangiffler.model.enums.Country;
import guru.qa.rangiffler.model.enums.TravelPhotoImage;
import guru.qa.rangiffler.page.components.FormAddNewPhoto;
import guru.qa.rangiffler.page.components.Header;
import guru.qa.rangiffler.page.components.PhotoGallery;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;

@ParametersAreNonnullByDefault
public final class TravelsPage extends BasePage<TravelsPage> {

  public static final @Nonnull String URL = CFG.frontUrl() + "my-travels";

  private final SelenideElement title = $(".MuiContainer-maxWidthLg .MuiTypography-h4").as("title");
  private final SelenideElement self = $(".MuiContainer-maxWidthLg >.MuiBox-root").as(TestIcon.TRAVELS);
  private final SelenideElement map = self.$(".worldmap__figure-container").as("Карта: " + TestIcon.TRAVELS);
  private final SelenideElement addPhotoBtn = self.$(".worldmap__figure-container + div button")
      .as("Кнопка: Add photo");

  private final SelenideElement myPhotoBtn = self.$("[value='my']").as("Button: Only my travels");
  private final SelenideElement withFriendsBtn = self.$("[value='friends']").as("Button: With Friends");

  @Getter
  private final PhotoGallery photoGallery = new PhotoGallery();

  @Getter
  private final Header header = new Header();

  @Step(TestIcon.TRAVELS + "Открываем")
  public static @Nonnull TravelsPage open() {
    return Selenide.open(URL, TravelsPage.class);
  }

  @Step(TestIcon.TRAVELS + "Проверка загрузки")
  public @Nonnull TravelsPage checkThatPageLoaded() {
    SelenideUtils.visible(self, map, addPhotoBtn, title);
    SelenideUtils.visibleAndInteractable(myPhotoBtn, withFriendsBtn);
    header.checkThatComponentLoaded();
    return this;
  }

  @Step(TestIcon.TRAVELS + "Открываем окно добавления фото")
  public @Nonnull FormAddNewPhoto clickAddPhoto() {
    addPhotoBtn.click();
    return new FormAddNewPhoto();
  }

  @Step(TestIcon.TRAVELS + "Переключение на вкладку: With Friends")
  public @Nonnull TravelsPage clickWithFriends() {
    withFriendsBtn.click();
    withFriendsBtn.shouldHave(attribute("aria-pressed", "true"));
    return this;
  }

  @Step(TestIcon.TRAVELS + "Добавляем фото")
  public @Nonnull TravelsPage addPhoto(TravelPhotoImage img, Country country, String description) {
    addPhotoBtn.click();
    new FormAddNewPhoto()
        .setCountry(country)
        .setDescription(description)
        .setPhoto(img)
        .save();
    return this;
  }
}
