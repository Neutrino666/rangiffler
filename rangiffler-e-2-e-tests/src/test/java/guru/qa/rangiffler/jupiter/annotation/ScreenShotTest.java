package guru.qa.rangiffler.jupiter.annotation;

import guru.qa.rangiffler.jupiter.extension.allure.ScreenShotTestExtension;
import guru.qa.rangiffler.model.enums.Image;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Test
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@ExtendWith(ScreenShotTestExtension.class)
public @interface ScreenShotTest {

  Image value() default Image.CYBER_DUCK;

  boolean rewriteExpected() default false;
}
