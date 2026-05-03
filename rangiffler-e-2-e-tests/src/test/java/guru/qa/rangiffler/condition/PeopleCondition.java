package guru.qa.rangiffler.condition;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.CollectionSource;
import guru.qa.rangiffler.model.UserJson;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.assertj.core.api.Assertions;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.opentest4j.AssertionFailedError;

@ParametersAreNonnullByDefault
public class PeopleCondition {

  public static @Nonnull WebElementsCondition containsAllPeopleInAnyOrder(final List<UserJson> people) {
    return new WebElementsCondition() {
      private final List<String> expected = convertToExpected(people);

      @Override
      public @Nonnull String toString() {
        return expected.toString();
      }

      @Override
      public @Nonnull CheckResult check(Driver driver, List<WebElement> elements) {
        List<String> actual = convertToActual(elements);
        if (expected.size() != actual.size()) {
          String message = String.format("List size mismatch (expected: %s, actual: %s)",
              expected.size(), actual.size());
          return rejected(message, actual.toString());
        }
        if (!actual.containsAll(expected)) {
          final String message = String.format("List users mismatch (expected: %s, actual: %s)",
              expected, actual);
          return rejected(message, actual.toString());
        }
        return accepted();
      }

      @Override
      public void fail(CollectionSource collection, CheckResult lastCheckResult,
          @Nullable Exception cause, long timeoutMs) {
        try {
          Assertions.assertThat((String) lastCheckResult.getActualValue())
              .as("Все строки таблицы people должны совпадать с ожидаемыми")
              .isEqualTo(toString());
        } catch (AssertionFailedError e) {
          throw new UIAssertionError(
              e.getMessage(),
              toString(), lastCheckResult.getActualValue()
          );
        }
      }
    };
  }

  private static @Nonnull List<String> convertToActual(final List<WebElement> elements) {
    return elements
        .stream()
        .map(row -> {
              List<WebElement> cells = row.findElements(By.cssSelector("td"));
              return Stream.of(
                      cells.get(0),
                      cells.get(1),
                      cells.get(2),
                      cells.get(3)
                  )
                  .map(WebElement::getText)
                  .toList()
                  .toString();
            }
        ).toList();
  }

  private static @Nonnull List<String> convertToExpected(final List<UserJson> users) {
    return users.stream()
        .map(u ->
            List.of(
                u.getUsername(),
                u.getFirstname(),
                u.getSurname(),
                u.getCountry().name()
            ).toString()
        )
        .toList();
  }
}
