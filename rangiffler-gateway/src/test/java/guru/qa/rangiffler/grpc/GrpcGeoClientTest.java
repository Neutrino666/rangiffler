package guru.qa.rangiffler.grpc;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.protobuf.Empty;
import guru.qa.rangiffler.grpc.RangifflerGeoServiceGrpc.RangifflerGeoServiceBlockingStub;
import guru.qa.rangiffler.service.api.GrpcGeoClient;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

public class GrpcGeoClientTest {

  private final String grpcCallErrorMessage = "503 SERVICE_UNAVAILABLE \"The gRPC operation was cancelled\"";
  private final RangifflerGeoServiceBlockingStub stub = mock(RangifflerGeoServiceBlockingStub.class);
  private final GrpcGeoClient grpcGeoClient = new GrpcGeoClient();
  private List<CountryResponse> testCountries;

  @BeforeEach
  void before() {
    ReflectionTestUtils.setField(grpcGeoClient, "stub", stub);
    final CountryResponse country1 = CountryResponse.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setCode("ru")
        .setName("Russian Federation")
        .setFlag("data:image/png;base64,1")
        .build();
    final CountryResponse country2 = CountryResponse.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setCode("lv")
        .setName("Latvia")
        .setFlag("data:image/png;base64,2")
        .build();
    testCountries = List.of(country1, country2);
    lenient().when(grpcGeoClient.getCountry("ru")).thenReturn(country1);
    lenient().when(grpcGeoClient.getCountries()).thenReturn(collectCountryPage());
  }

  @Test
  void getCountryShouldReturnCountryResponse() {
    final CountryResponse expected = testCountries.getFirst();
    final CountryResponse actual = grpcGeoClient.getCountry(expected.getCode());
    verify(stub).getCountry(
        CountryRequest.newBuilder()
            .setCode(expected.getCode())
            .build()
    );
    assertThat(actual)
        .isNotNull()
        .hasNoNullFieldsOrProperties()
        .isEqualTo(expected);
  }

  @Test
  void getCountryShouldThrowResponseStatusException() {
    when(stub.getCountry(any(CountryRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));
    assertThatThrownBy(() -> grpcGeoClient.getCountry(""))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessage(grpcCallErrorMessage)
        .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
        .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
  }

  @Test
  void getCountriesShouldReturnCountryResponseList() {
    final CountryPageResponse expected = collectCountryPage();
    final CountryPageResponse actual = grpcGeoClient.getCountries();
    verify(stub).getCountries(Empty.newBuilder().build());
    assertThat(actual.getAllCountriesList())
        .isNotEmpty()
        .hasSize(2);
    assertThat(actual)
        .hasNoNullFieldsOrProperties()
        .isEqualTo(expected);
  }

  @Test
  void getCountriesThrowResponseStatusException() {
    when(stub.getCountries(any(Empty.class)))
        .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));
    assertThatThrownBy(grpcGeoClient::getCountries)
        .isInstanceOf(ResponseStatusException.class)
        .hasMessage(grpcCallErrorMessage)
        .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
        .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
  }

  private CountryPageResponse collectCountryPage() {
    return CountryPageResponse.newBuilder()
        .addAllAllCountries(testCountries)
        .build();
  }
}
