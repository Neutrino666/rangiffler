package guru.qa.rangiffler.service.utils;

import com.google.protobuf.MessageOrBuilder;
import io.grpc.StatusRuntimeException;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.NONE)
public class GrpcCall {

  public static <T extends MessageOrBuilder> T execute(Supplier<T> grpcCall) {
    try {
      return grpcCall.get();
    } catch (StatusRuntimeException e) {
      log.error("### Error while calling gRPC server ", e);
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
    }
  }
}
