package guru.qa.rangiffler.service.api;

import com.google.protobuf.Empty;
import guru.qa.rangiffler.grpc.CountryPageResponse;
import guru.qa.rangiffler.grpc.CountryRequest;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.RangifflerGeoServiceGrpc;
import guru.qa.rangiffler.service.utils.GrpcCall;
import javax.annotation.ParametersAreNonnullByDefault;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@ParametersAreNonnullByDefault
public final class GrpcGeoClient {

  private static final Empty EMPTY = Empty.getDefaultInstance();

  @GrpcClient("grpcGeoClient")
  private RangifflerGeoServiceGrpc.RangifflerGeoServiceBlockingStub stub;
  private final GrpcCall grpcCall = new GrpcCall();

  @Cacheable("getCountries")
  public CountryPageResponse getCountries() {
    return grpcCall.execute(() ->
        stub.getCountries(EMPTY)
    );
  }

  @Cacheable(value = "getCountry", key = "#code")
  public CountryResponse getCountry(String code) {
    return grpcCall.execute(() ->
        stub.getCountry(CountryRequest.newBuilder().setCode(code).build())
    );
  }
}
