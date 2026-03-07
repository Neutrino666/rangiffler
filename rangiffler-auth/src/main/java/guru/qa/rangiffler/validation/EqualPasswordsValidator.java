package guru.qa.rangiffler.validation;

import guru.qa.rangiffler.model.RegistrationForm;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EqualPasswordsValidator implements ConstraintValidator<EqualPasswords, RegistrationForm> {

  @Override
  public boolean isValid(RegistrationForm form, ConstraintValidatorContext context) {
    boolean isValid = form.password().equals(form.passwordSubmit());
    if (!isValid) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
          .addPropertyNode("password")
          .addConstraintViolation();
    }
    return isValid;
  }
}
