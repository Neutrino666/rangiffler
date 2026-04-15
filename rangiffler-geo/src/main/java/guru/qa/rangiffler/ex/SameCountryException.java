package guru.qa.rangiffler.ex;

public class SameCountryException extends RuntimeException {
  public SameCountryException(String code) {
    super("Can`t find country by code: '%s'" .formatted(code));
  }
}
