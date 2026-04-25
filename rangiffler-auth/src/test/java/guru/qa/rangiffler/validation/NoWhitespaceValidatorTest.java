package guru.qa.rangiffler.validation;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

public class NoWhitespaceValidatorTest {

  private final NoWhitespaceValidator validator = new NoWhitespaceValidator();
  private final ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);

  @ValueSource(strings = {
      "foo ", "foo bar", " foo"
  })
  @ParameterizedTest
  void shouldReturnFalseForStringsWithSpaces(String input) {
    assertThat(validator.isValid(input, context))
        .isFalse();
  }

  @Test
  void shouldReturnTrueForStringWithoutSpaces() {
    assertThat(validator.isValid("test", context))
        .isTrue();
  }
}
