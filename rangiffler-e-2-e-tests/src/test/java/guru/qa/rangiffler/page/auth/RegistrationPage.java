package guru.qa.rangiffler.page.auth;

import guru.qa.rangiffler.page.BasePage;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@ParametersAreNonnullByDefault
public final class RegistrationPage extends BasePage<LoginPage> {

  public static final @Nonnull String URL = CFG.authUrl() + "register";

}
