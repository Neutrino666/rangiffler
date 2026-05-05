package guru.qa.rangiffler.jupiter.annotation;

import guru.qa.rangiffler.model.enums.Country;
import guru.qa.rangiffler.model.enums.TravelPhotoImage;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Photo {

  TravelPhotoImage img() default TravelPhotoImage.BAIKAL;

  int likes() default 0;

  Country country() default Country.RU;

  String description() default "";
}
