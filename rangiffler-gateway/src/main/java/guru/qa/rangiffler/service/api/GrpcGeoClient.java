package guru.qa.rangiffler.service.api;

import com.google.protobuf.Empty;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.RangifflerCountryServiceGrpc;
import io.grpc.StatusRuntimeException;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
@ParametersAreNonnullByDefault
public class GrpcGeoClient {

  @GrpcClient("grpcGeoClient")
  private RangifflerCountryServiceGrpc.RangifflerCountryServiceBlockingStub rangifflerCountryServiceBlockingStub;
  private static final Empty EMPTY = Empty.getDefaultInstance();

  public CountryResponse getCountries() {
    try {
      return rangifflerCountryServiceBlockingStub.getCountries(EMPTY);
    } catch (StatusRuntimeException e) {
      log.error("### Error while calling gRPC server ", e);
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
    }
  }
}
