package guru.qa.rangiffler.service.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import com.google.protobuf.Empty;
import guru.qa.rangiffler.data.CountryEntity;
import guru.qa.rangiffler.data.repository.CountryRepository;
import guru.qa.rangiffler.ex.SameCountryException;
import guru.qa.rangiffler.grpc.CountryPageResponse;
import guru.qa.rangiffler.grpc.CountryRequest;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.service.GeoService;
import guru.qa.rangiffler.service.GrpcGeoService;
import guru.qa.rangiffler.util.ByteAsString;
import io.grpc.stub.StreamObserver;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GrpcGeoServiceTest {

  @Mock
  private CountryRepository countryRepository;

  @Mock
  private StreamObserver<CountryResponse> countryResponse;

  @Mock
  private StreamObserver<CountryPageResponse> countryPageResponse;

  @InjectMocks
  private GrpcGeoService grpcGeoService;

  @Captor
  private ArgumentCaptor<CountryResponse> countryResponseCaptor;

  @Captor
  private ArgumentCaptor<CountryPageResponse> countriesPageResponseCaptor;

  private List<CountryEntity> testCountries;

  @BeforeEach
  void before() {
    final CountryEntity country1 = new CountryEntity();
    final CountryEntity country2 = new CountryEntity();

    country1.setId(UUID.randomUUID());
    country1.setCode("ru");
    country1.setName("Russian Federation");
    country1.setFlag("data:image/png;base64,1".getBytes(StandardCharsets.UTF_8));

    country2.setId(UUID.randomUUID());
    country2.setCode("lv");
    country2.setName("Latvia");
    country2.setFlag("data:image/png;base64,2".getBytes(StandardCharsets.UTF_8));

    testCountries = List.of(country1, country2);
    lenient().when(countryRepository.findAll())
        .thenReturn(testCountries);
    lenient().when(countryRepository.findByCode("ru"))
        .thenReturn(Optional.of(country1));
    lenient().when(countryRepository.findByCode("lv"))
        .thenReturn(Optional.of(country2));

    grpcGeoService = new GrpcGeoService(new GeoService(countryRepository));
  }

  @Test
  void shouldExistCountriesPositive() {
    grpcGeoService.getCountries(Empty.newBuilder().build(), countryPageResponse);
    verify(countryRepository).findAll();
    verify(countryPageResponse).onNext(countriesPageResponseCaptor.capture());
    verify(countryPageResponse).onCompleted();

    final CountryPageResponse actual = countriesPageResponseCaptor.getValue();
    final List<CountryResponse> expectedCountries = testCountries.stream()
        .map(this::countryFromEntity)
        .toList();

    assertThat(actual)
        .hasNoNullFieldsOrProperties();
    assertThat(actual.getAllCountriesList())
        .isNotEmpty()
        .hasSize(2)
        .containsExactlyInAnyOrderElementsOf(expectedCountries);
  }

  @Test
  void shouldExistCountry() {
    final CountryEntity firstCountry = testCountries.getFirst();
    final CountryRequest request = CountryRequest.newBuilder()
        .setCode(firstCountry.getCode())
        .build();
    grpcGeoService.getCountry(request, countryResponse);
    verify(countryRepository).findByCode(firstCountry.getCode());
    verify(countryResponse).onNext(countryResponseCaptor.capture());
    verify(countryResponse).onCompleted();

    final CountryResponse actual = countryFromEntity(testCountries.getFirst());
    final CountryResponse expected = countryFromEntity(testCountries.getFirst());

    assertThat(actual)
        .isNotNull()
        .hasNoNullFieldsOrProperties()
        .isEqualTo(expected);
  }

  @Test
  void shouldThrowIfNotExistCountry() {
    final String badCode = "notFound";
    final CountryRequest request = CountryRequest.newBuilder()
        .setCode(badCode)
        .build();
    assertThatThrownBy(() -> grpcGeoService.getCountry(request, countryResponse))
        .isInstanceOf(SameCountryException.class)
        .hasMessage("Can`t find country by code: '%s'".formatted(badCode));
  }

  private CountryResponse countryFromEntity(@Nonnull final CountryEntity ce) {
    return CountryResponse.newBuilder()
        .setId(ce.getId().toString())
        .setCode(ce.getCode())
        .setName(ce.getName())
        .setFlag(new ByteAsString(ce.getFlag()).string())
        .build();
  }
}
