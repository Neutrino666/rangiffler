package guru.qa.rangiffler.jupiter.extension;

import guru.qa.rangiffler.api.rest.core.ThreadSafeCookieStore;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

@ParametersAreNonnullByDefault
public final class CookieExtension implements AfterTestExecutionCallback {

  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {
    ThreadSafeCookieStore.INSTANCE.removeAll();
  }
}
