package guru.qa.rangiffler.model;

public interface TestPrefix {

  String POSITIVE = "[Positive] ";
  String NEGATIVE = "[Negative] ";

  String GRAPHQL = "[GRAPHQL] ";
  String GRPC = "[GRPC] ";

  String UI = "[UI] ";
  String UI_POSITIVE = UI + POSITIVE;
  String UI_NEGATIVE = UI + NEGATIVE;

  String REST = "[REST] ";
  String REST_NEGATIVE = REST + NEGATIVE;
  String REST_POSITIVE = REST + POSITIVE;
}
