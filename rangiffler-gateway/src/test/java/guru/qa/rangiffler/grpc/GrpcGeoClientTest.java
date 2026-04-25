package guru.qa.rangiffler.grpc;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.google.protobuf.Empty;
import guru.qa.rangiffler.grpc.RangifflerGeoServiceGrpc.RangifflerGeoServiceBlockingStub;
import guru.qa.rangiffler.service.api.GrpcGeoClient;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class GrpcGeoClientTest {

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

  private CountryPageResponse collectCountryPage() {
    return CountryPageResponse.newBuilder()
        .addAllAllCountries(testCountries)
        .build();
  }
}