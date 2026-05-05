package guru.qa.rangiffler.page.components;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.ElementsCollection;
import guru.qa.rangiffler.condition.PhotoCardCondition;
import guru.qa.rangiffler.model.PhotoCardJson;
import guru.qa.rangiffler.model.TestIcon;
import guru.qa.rangiffler.page.TravelsPage;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class PhotoGallery extends BaseComponent<Header> {

  private final ElementsCollection photoCards = self.$$(".MuiPaper-elevation").as("Список фото");
  private final ElementsCollection prevNextButtons = self.$$(".MuiGrid-spacing-xs-3 + div button")
      .as("Кнопки: previous и next");


  public PhotoGallery() {
    super($(".MuiContainer-maxWidthLg>.MuiContainer-maxWidthLg").as(TestIcon.TRAVEL_PHOTO));
  }

  @Nonnull
  @Step(TestIcon.TRAVEL_PHOTO + "Проверяем наличие всех фото в любой последовательности")
  public PhotoGallery exactlyPhotoCardsInAnyOrder(PhotoCardJson... photoCardsJson) {
    photoCards.shouldHave(PhotoCardCondition.exactlyPhotoCardsInAnyOrder(photoCardsJson));
    return this;
  }

  @Nonnull
  @Step(TestIcon.TRAVEL_PHOTO + "Удаляем фото")
  public PhotoGallery deletePhotoByDescription(String text) {
    findPhotoCardByText(text).delete();
    return this;
  }

  @Nonnull
  public TravelsPage getTravelsPage() {
    return new TravelsPage();
  }

  @Nonnull
  @Step(TestIcon.TRAVEL_PHOTO + "Удаляем фото")
  public PhotoCard findPhotoCardByText(String text) {
    return new PhotoCard(photoCards.findBy(text(text)).as("Photo with text: " + text));
  }
}
