package guru.qa.rangiffler.service;

import com.google.protobuf.Empty;
import guru.qa.rangiffler.data.CountryEntity;
import guru.qa.rangiffler.data.repository.CountryRepository;
import guru.qa.rangiffler.grpc.CountryPageResponse;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.RangifflerCountryServiceGrpc;
import guru.qa.rangiffler.util.ByteAsString;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@GrpcService
public class GrpcCountryService extends RangifflerCountryServiceGrpc.RangifflerCountryServiceImplBase {

  private final CountryRepository countryRepository;

  @Autowired
  public GrpcCountryService(CountryRepository countryRepository) {
    this.countryRepository = countryRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public void getCountries(Empty request, StreamObserver<CountryPageResponse> responseObserver) {
    CountryPageResponse response = CountryPageResponse.newBuilder()
        .addAllAllCountries(
            countryRepository.findAll().stream()
                .map(this::countryFromEntity)
                .toList()
        )
        .build();
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
