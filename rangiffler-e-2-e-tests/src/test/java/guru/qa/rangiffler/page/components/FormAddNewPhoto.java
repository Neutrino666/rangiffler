package guru.qa.rangiffler.page.components;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.helpers.SelenideUtils;
import guru.qa.rangiffler.model.TestIcon;
import guru.qa.rangiffler.model.enums.Country;
import guru.qa.rangiffler.model.enums.TravelPhotoImage;
import guru.qa.rangiffler.page.TravelsPage;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class FormAddNewPhoto extends BaseComponent<FormAddNewPhoto> {

  private final SelenideElement title = self.$(".MuiDialogTitle-root").as("Title");
  private final SelenideElement imageInput = self.$("#image__input").as("Image input");
  private final SelenideElement description = self.$("#description").as("Description input");
  private final SelenideElement saveBtn = self.$("[type='submit']").as("Save button");
  private final SelenideElement closeBtn = self.$("[type='button']").as("Close button");
  private final SelenideElement countriesDropdown = self.$("#country").as("Countries dropdown");
  private final SelenideElement errorMsg = self.$("[for='image__input'] + div").as("Error message");


  private final ElementsCollection countries = $$("li[data-value]").as("Countries");


  public FormAddNewPhoto() {
    super($("[aria-describedby='alert-dialog-slide-description']"));
  }

  @Step(TestIcon.TRAVEL_PHOTO + "Проверка загрузки")
  public @Nonnull FormAddNewPhoto checkThatPageLoaded() {
    SelenideUtils.visible(self, title);
    SelenideUtils.exist(imageInput, countriesDropdown);
    SelenideUtils.visibleAndInteractable(saveBtn, countriesDropdown, description);
    return this;
  }

  @Step(TestIcon.TRAVEL_PHOTO + "Set description")
  public @Nonnull FormAddNewPhoto setDescription(String text) {
    description.val(text);
    return this;
  }

  @Step(TestIcon.TRAVEL_PHOTO + "Set photo")
  public @Nonnull FormAddNewPhoto setPhoto(TravelPhotoImage img) {
    imageInput.uploadFromClasspath(img.getDirResources());
    return this;
  }

  @Step(TestIcon.TRAVEL_PHOTO + "Set country")
  public @Nonnull FormAddNewPhoto setCountry(Country country) {
    countriesDropdown.click();
    countries.findBy(attribute("data-value", country.getCode()))
        .click();
    return this;
  }

  @Step(TestIcon.TRAVEL_PHOTO + "Save")
  public @Nonnull FormAddNewPhoto save() {
    saveBtn.click();
    return this;
  }

  @Step(TestIcon.TRAVEL_PHOTO + "Check error message")
  public @Nonnull FormAddNewPhoto checkErrorMessage(String msg) {
    errorMsg.shouldHave(text(msg));
    return this;
  }

  @Step(TestIcon.TRAVEL_PHOTO + "Close add photo")
  public @Nonnull TravelsPage close() {
    closeBtn.click();
    return new TravelsPage();
  }
}
