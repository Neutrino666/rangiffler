package guru.qa.rangiffler.page.components;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.helpers.SelenideUtils;
import guru.qa.rangiffler.model.PhotoCardJson;
import guru.qa.rangiffler.model.TestIcon;
import guru.qa.rangiffler.page.TravelsPage;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.openqa.selenium.Keys;

@ParametersAreNonnullByDefault
public final class FormEditPhoto extends BaseComponent<FormAddNewPhoto> {

  private final SelenideElement title = self.$(".MuiDialogTitle-root").as("Title");
  private final SelenideElement imageInput = self.$("#image__input").as("Image input");
  private final SelenideElement description = self.$("#description").as("Description input");
  private final SelenideElement saveBtn = self.$("[type='submit']").as("Save button");
  private final SelenideElement closeBtn = self.$("[type='button']").as("Close button");
  private final SelenideElement countriesDropdown = self.$("#country").as("Countries dropdown");

  private final ElementsCollection countries = $$("li[data-value]").as("Countries");

  public FormEditPhoto() {
    super($("[aria-describedby='alert-dialog-slide-description']"));
  }

  @Step(TestIcon.TRAVEL_PHOTO + "Проверка загрузки")
  public @Nonnull FormEditPhoto checkThatPageLoaded() {
    SelenideUtils.visible(self, title);
    SelenideUtils.exist(imageInput, countriesDropdown);
    SelenideUtils.visibleAndInteractable(saveBtn, countriesDropdown, description);
    return this;
  }

  @Step(TestIcon.TRAVEL_PHOTO + "Update photo")
  public TravelsPage update(PhotoCardJson photo) {
    imageInput.uploadFromClasspath(photo.img().getDirResources());
    countriesDropdown.click();
    countries.findBy(attribute("data-value", photo.country().getCode()))
        .click();
    clearDescription();
    description.val(photo.description());
    saveBtn.click();
    return new TravelsPage();
  }

  // setValue() - не работает, после клика или доп ввода возвращается старое значение
  private void clearDescription() {
    description.getValue().chars().forEach(c -> description.sendKeys(Keys.BACK_SPACE));
    ;
  }
}
