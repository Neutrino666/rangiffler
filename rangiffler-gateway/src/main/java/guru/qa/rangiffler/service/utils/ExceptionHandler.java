package guru.qa.rangiffler.service.utils;

import static guru.qa.rangiffler.service.utils.ValidationExceptionMessageResolver.resolveHandlerMethodValidationException;
import static guru.qa.rangiffler.service.utils.ValidationExceptionMessageResolver.resolveMethodArgumentNotValidException;
import static java.util.Objects.requireNonNull;

import guru.qa.rangiffler.service.model.ErrorJson;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
public class ExceptionHandler extends ResponseEntityExceptionHandler {

  private static final String ENTITY_VALIDATION_ERROR = "Entity validation error";

  @Value("${spring.application.name}")
  private String appName;

  @Override
  protected @Nonnull ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    return ResponseEntity
        .status(status)
        .body(new ErrorJson(
            appName + ": " + ENTITY_VALIDATION_ERROR,
            HttpStatus.resolve(status.value()).getReasonPhrase(),
            status.value(),
            resolveMethodArgumentNotValidException(ex),
            ((ServletWebRequest) request).getRequest().getRequestURI()
        ));
  }

  @Override
  protected @Nonnull ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    return ResponseEntity
        .status(status)
        .body(new ErrorJson(
            appName + ": " + ENTITY_VALIDATION_ERROR,
            requireNonNull(HttpStatus.resolve(status.value())).getReasonPhrase(),
            status.value(),
            resolveHandlerMethodValidationException(ex),
            ((ServletWebRequest) request).getRequest().getRequestURI()
        ));
  }

  private @Nonnull ResponseEntity<ErrorJson> withStatus(String type,
      HttpStatus status,
      String message,
      HttpServletRequest request) {
    return ResponseEntity
        .status(status)
        .body(new ErrorJson(
            appName + ": " + type,
            status.getReasonPhrase(),
            status.value(),
            message,
            request.getRequestURI()
        ));
  }

  @Nonnull
  private ResponseEntity<ErrorJson> handleForwardedException(HttpClientErrorException ex, HttpServletRequest request) {
    ErrorJson originalError = ex.getResponseBodyAs(ErrorJson.class);
    return ResponseEntity
        .status(requireNonNull(originalError).status())
        .body(new ErrorJson(
            originalError.type(),
            originalError.title(),
            originalError.status(),
            originalError.detail(),
            request.getRequestURI()
        ));
  }
}