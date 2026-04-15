package guru.qa.rangiffler.service;

import com.google.protobuf.Empty;
import guru.qa.rangiffler.data.CountryEntity;
import guru.qa.rangiffler.ex.SameCountryException;
import guru.qa.rangiffler.grpc.CountryPageResponse;
import guru.qa.rangiffler.grpc.CountryRequest;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.RangifflerGeoServiceGrpc;
import guru.qa.rangiffler.util.ByteAsString;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@GrpcService
public class GrpcGeoService extends RangifflerGeoServiceGrpc.RangifflerGeoServiceImplBase {

  private final GeoService geoService;

  @Autowired
  public GrpcGeoService(GeoService geoService) {
    this.geoService = geoService;
  }

  @Override
  @Transactional(readOnly = true)
  public void getCountries(Empty request, StreamObserver<CountryPageResponse> responseObserver) {
    CountryPageResponse response = CountryPageResponse.newBuilder()
        .addAllAllCountries(
            geoService.countries().stream()
                .map(this::countryFromEntity)
                .toList()
        )
        .build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getCountry(CountryRequest request, StreamObserver<CountryResponse> responseObserver) {
    CountryResponse response = geoService.getCountry(request.getCode())
        .map(this::countryFromEntity)
        .orElseThrow(() -> new SameCountryException(request.getCode()));
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  private CountryResponse countryFromEntity(CountryEntity ce) {
    return CountryResponse.newBuilder()
        .setId(ce.getId().toString())
        .setCode(ce.getCode())
        .setName(ce.getName())
        .setFlag(new ByteAsString(ce.getFlag()).string())
        .build();
  }
}
