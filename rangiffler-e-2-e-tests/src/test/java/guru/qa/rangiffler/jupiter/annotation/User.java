package guru.qa.rangiffler.jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface User {

  String username() default "";

  Photo[] myPhotos() default {};

  int friends() default 0;

  int incomeInvitations() default 0;

  int outcomeInvitations() default 0;

  int emptyPeople() default 0;

  Photo[] friendsPhotos() default {};
}
