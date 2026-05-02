package guru.qa.rangiffler.jupiter.annotation;

import guru.qa.rangiffler.model.CountryCode;
import guru.qa.rangiffler.model.PhotoFile;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Photo {

  PhotoFile file() default PhotoFile.MAN;

  CountryCode code() default CountryCode.RU;
}
