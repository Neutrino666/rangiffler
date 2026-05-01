package guru.qa.rangiffler.test.rest;

import guru.qa.rangiffler.jupiter.extension.ApiLoginExtension;
import guru.qa.rangiffler.jupiter.meta.RestTest;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.extension.RegisterExtension;

@RestTest
@ParametersAreNonnullByDefault
public class BaseRestTest {

  @RegisterExtension
  protected static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();
}
