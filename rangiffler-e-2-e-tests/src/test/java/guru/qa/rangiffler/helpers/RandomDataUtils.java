package guru.qa.rangiffler.helpers;

import com.github.javafaker.Faker;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class RandomDataUtils {

  private static final Faker faker = new Faker();

  @Nonnull
  public static String getRandomUserName() {
    return faker.name().username();
  }

  @Nonnull
  public static String getRandomName() {
    return faker.name().name();
  }

  @Nonnull
  public static String getRandomTravelDescription() {
    return "travel with " + faker.funnyName().name();
  }

  @Nonnull
  public static String getRandomSurname() {
    return faker.name().lastName();
  }

  @Nonnull
  public static String getRandomPassword() {
    return faker.internet().password(3, 12);
  }

  @Nonnull
  public static String getRandomPassword(int min, int max) {
    return faker.internet().password(min, max);
  }
}
