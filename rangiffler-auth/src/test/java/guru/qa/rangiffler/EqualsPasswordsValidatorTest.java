package guru.qa.rangiffler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import guru.qa.rangiffler.model.RegistrationForm;
import guru.qa.rangiffler.validation.EqualPasswordsValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EqualsPasswordsValidatorTest {

  private final EqualPasswordsValidator equalPasswordsValidator = new EqualPasswordsValidator();

  @Test
  void isValidTest(@Mock ConstraintValidatorContext context) {
    RegistrationForm rf = new RegistrationForm(
        "newUser",
        "12345",
        "12345"
    );
    assertThat(equalPasswordsValidator.isValid(rf, context))
        .isTrue();
  }

  @Test
  void isValidNegativeTest(
      @Mock ConstraintValidatorContext context,
      @Mock ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder,
      @Mock ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilderCustomizableContext
  ) {
    when(context.buildConstraintViolationWithTemplate(any()))
        .thenReturn(constraintViolationBuilder);

    when(constraintViolationBuilder.addPropertyNode(eq("password")))
        .thenReturn(nodeBuilderCustomizableContext);

    RegistrationForm rf = new RegistrationForm(
        "newUser",
        "12345",
        "123451231"
    );

    assertThat(equalPasswordsValidator.isValid(rf, context))
        .isFalse();
  }
}
