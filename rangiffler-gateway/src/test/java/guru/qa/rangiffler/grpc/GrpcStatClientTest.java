package guru.qa.rangiffler.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import guru.qa.rangiffler.grpc.RangifflerPhotoServiceGrpc.RangifflerPhotoServiceBlockingStub;
import guru.qa.rangiffler.service.api.GrpcStatClient;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.List;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

@ParametersAreNonnullByDefault
public class GrpcStatClientTest {

  private final String grpcCallErrorMessage = "503 SERVICE_UNAVAILABLE \"The gRPC operation was cancelled\"";
  private final RangifflerPhotoServiceBlockingStub stub = mock(RangifflerPhotoServiceBlockingStub.class);
  private final GrpcStatClient grpcStatClient = new GrpcStatClient();
  private StatResponse statResponseWithoutFriends;
  private StatResponse statResponseWithFriends;

  @BeforeEach
  void before() {
    ReflectionTestUtils.setField(grpcStatClient, "stub", stub);
    final CountryStat countryStat1 = CountryStat.newBuilder()
        .setCount(1)
        .setCountry(CountryValues.RU)
        .build();
    final CountryStat countryStat2 = CountryStat.newBuilder().build();

    statResponseWithoutFriends = StatResponse.newBuilder()
        .addAllStat(List.of(countryStat1))
        .build();
    statResponseWithFriends = StatResponse.newBuilder()
        .addAllStat(List.of(countryStat1, countryStat2))
        .build();
  }

  @Test
  void statsReturnShouldReturnStatWithFriends() {
    final StatRequest request = statRequestByWithFriend(true);
    when(stub.stat(request)).thenReturn(statResponseWithFriends);
    final StatResponse actual = grpcStatClient.stat(request);

    verify(stub).stat(request);
    assertThat(actual).isEqualTo(statResponseWithFriends);
  }

  @Test
  void statsReturnShouldReturnStatWithoutFriends() {
    final StatRequest request = statRequestByWithFriend(false);
    when(stub.stat(request)).thenReturn(statResponseWithoutFriends);
    final StatResponse actual = grpcStatClient.stat(request);

    verify(stub).stat(request);
    assertThat(actual).isEqualTo(statResponseWithoutFriends);
  }

  @Test
  void statsReturnShouldThrowResponseStatusException() {
    when(stub.stat(any(StatRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));
    assertThatThrownBy(() -> grpcStatClient.stat(StatRequest.newBuilder().build()))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessage(grpcCallErrorMessage)
        .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
        .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
  }

  private StatRequest statRequestByWithFriend(boolean withFriends) {
    return StatRequest.newBuilder()
        .setUserId(UUID.randomUUID().toString())
        .setUsername("test")
        .setWithFriends(withFriends)
        .build();
  }
}
