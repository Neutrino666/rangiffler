package guru.qa.rangiffler.helpers;

import guru.qa.rangiffler.jupiter.extension.allure.ScreenShotTestExtension;
import guru.qa.rangiffler.jupiter.extension.allure.ScreenShotTestExtension.Type;
import java.awt.image.BufferedImage;
import java.util.function.BooleanSupplier;
import javax.annotation.ParametersAreNonnullByDefault;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;

@ParametersAreNonnullByDefault
public final class ScreenDiffResult implements BooleanSupplier {

  public static final Integer ALLOWED_DIFF_PIXELS = 300;
  private final BufferedImage expected;
  private final BufferedImage actual;
  private final ImageDiff diff;
  private final boolean hasDiff;

  public ScreenDiffResult(BufferedImage expected, BufferedImage actual) {
    this.expected = expected;
    this.actual = actual;
    this.diff = new ImageDiffer().makeDiff(expected, actual);
    this.hasDiff = diff.getDiffSize() > ALLOWED_DIFF_PIXELS;
  }

  @Override
  public boolean getAsBoolean() {
    if (hasDiff) {
      ScreenShotTestExtension.storeSet(Type.EXPECTED, expected);
      ScreenShotTestExtension.storeSet(Type.ACTUAL, actual);
      ScreenShotTestExtension.storeSet(Type.DIFF, diff.getMarkedImage());
    }
    return hasDiff;
  }
}
