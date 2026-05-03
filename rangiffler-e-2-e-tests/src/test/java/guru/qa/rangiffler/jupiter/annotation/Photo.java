package guru.qa.rangiffler.jupiter.annotation;

import guru.qa.rangiffler.model.enums.Country;
import guru.qa.rangiffler.model.enums.Image;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Photo {

  Image file() default Image.CYBER_DUCK;

  Country code() default Country.RU;
}
