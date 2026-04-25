package guru.qa.rangiffler.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import guru.qa.rangiffler.grpc.RangifflerPhotoServiceGrpc.RangifflerPhotoServiceBlockingStub;
import guru.qa.rangiffler.service.api.GrpcStatClient;
import java.util.List;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@ParametersAreNonnullByDefault
public class GrpcStatClientTest {

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

  private StatRequest statRequestByWithFriend(boolean withFriends) {
    return StatRequest.newBuilder()
        .setUserId(UUID.randomUUID().toString())
        .setUsername("test")
        .setWithFriends(withFriends)
        .build();
  }
}
