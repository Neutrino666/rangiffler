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
public class GrpcGeoClient {

  @GrpcClient("grpcGeoClient")
  private RangifflerGeoServiceGrpc.RangifflerGeoServiceBlockingStub rangifflerGeoServiceBlockingStub;
  private static final Empty EMPTY = Empty.getDefaultInstance();

  @Cacheable("getCountries")
  public CountryPageResponse getCountries() {
    return GrpcCall.execute(() ->
        rangifflerGeoServiceBlockingStub.getCountries(EMPTY)
    );
  }

  @Cacheable(value = "getCountry", key = "#code")
  public CountryResponse getCountry(String code) {
    return GrpcCall.execute(() ->
        rangifflerGeoServiceBlockingStub.getCountry(CountryRequest.newBuilder().setCode(code).build())
    );
  }
}
