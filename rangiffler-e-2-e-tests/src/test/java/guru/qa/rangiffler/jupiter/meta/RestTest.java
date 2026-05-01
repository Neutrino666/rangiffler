package guru.qa.rangiffler.jupiter.meta;

import guru.qa.rangiffler.jupiter.extension.UserExtension;
import io.qameta.allure.junit5.AllureJunit5;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({
    AllureJunit5.class,
    UserExtension.class,
})
public @interface RestTest {

}
