package guru.qa.rangiffler.page.components;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.model.TestIcon;
import guru.qa.rangiffler.page.TravelsPage;
import io.qameta.allure.Step;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class PhotoCard extends BaseComponent<Header> {

  private final SelenideElement heartBeforeLikes = self.$("[data-testid='FavoriteOutlinedIcon']")
      .as("heart before likes");
  private final SelenideElement src = self.$(".photo-card__image").as("Image src");
  private final SelenideElement description = self.$(".photo-card__content").as("Description");
  private final SelenideElement likeBtn = self.$("[aria-label='like']").as("like button");

  private final SelenideElement deleteBtn = self.$(".MuiButton-outlinedPrimary").as("Delete button");
  private final SelenideElement editBtn = self.$(".MuiButton-containedPrimary").as("Edit button");


  private final ElementsCollection buttons = self.$$("button.MuiButton-sizeMedium")
      .as("Edit/Delete buttons");

  public PhotoCard(SelenideElement self) {
    super(self);
  }

  @Step(TestIcon.TRAVEL_PHOTO + "Удаление фото")
  public PhotoGallery delete() {
    deleteBtn.click();
    return new PhotoGallery();
  }

  @Step(TestIcon.TRAVEL_PHOTO + "Удаление фото")
  public FormEditPhoto clickEdit() {
    editBtn.click();
    return new FormEditPhoto();
  }

  @Step(TestIcon.TRAVEL_PHOTO + "Лайк фото")
  public PhotoCard tapLike() {
    likeBtn.click();
    return this;
  }

  public TravelsPage getTravelsPage() {
    return new TravelsPage();
  }
}
