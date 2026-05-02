package guru.qa.rangiffler.jupiter.annotation;

import guru.qa.rangiffler.model.Country;
import guru.qa.rangiffler.model.PhotoFile;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Photo {

  PhotoFile file() default PhotoFile.CYBER_DUCK;

  Country code() default Country.RU;
}
