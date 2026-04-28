package guru.qa.rangiffler.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import guru.qa.rangiffler.grpc.RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub;
import guru.qa.rangiffler.grpc.UsersRequest;
import guru.qa.rangiffler.grpc.UsersResponse;
import java.util.List;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ParametersAreNonnullByDefault
@ExtendWith(MockitoExtension.class)
class GrpcUserdataClientTest {

  @Mock
  private RangifflerUserdataServiceBlockingStub stub;

  @InjectMocks
  private GrpcUserdataClient grpcUserdataClient;

  @Captor
  private ArgumentCaptor<UsersRequest> usersRequestCaptor;

  private final UUID userId = UUID.randomUUID();
  private final String username = "testUserName";

  @Test
  void photoAccessUsersShouldReturnUserIdOnlyWhenNoFriendsExist() {
    when(stub.listFriendsIds(any(UsersRequest.class)))
        .thenReturn(UsersResponse.newBuilder().build());

    final List<UUID> result = grpcUserdataClient.photoAccessUsers(userId, username);

    assertThat(result).containsExactly(userId);
  }

  @Test
  void photoAccessUsersShouldReturnUserIdAndFriendIdsWhenFriendsExist() {
    final UUID friendId1 = UUID.randomUUID();
    final UUID friendId2 = UUID.randomUUID();
    final UsersResponse response = UsersResponse.newBuilder()
        .addId(friendId1.toString())
        .addId(friendId2.toString())
        .build();
    final List<UUID> expected = List.of(userId, friendId1, friendId2);

    when(stub.listFriendsIds(any(UsersRequest.class)))
        .thenReturn(response);

    final List<UUID> result = grpcUserdataClient.photoAccessUsers(userId, username);

    assertThat(result)
        .containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  void photoAccessUsersShouldPassCorrectUsernameToGrpcCall() {
    when(stub.listFriendsIds(any(UsersRequest.class)))
        .thenReturn(UsersResponse.newBuilder().build());

    grpcUserdataClient.photoAccessUsers(userId, username);

    verify(stub).listFriendsIds(usersRequestCaptor.capture());
    assertThat(usersRequestCaptor.getValue())
        .isEqualTo(usersRequest());
  }

  @Test
  void photoAccessUsersShouldReturnUserIdFirstInList() {
    final UUID friendId = UUID.randomUUID();
    final UsersResponse usersResponse = UsersResponse.newBuilder()
        .addId(friendId.toString())
        .build();
    when(stub.listFriendsIds(any(UsersRequest.class)))
        .thenReturn(usersResponse);

    final List<UUID> result = grpcUserdataClient.photoAccessUsers(userId, username);

    assertThat(result).containsExactlyInAnyOrder(userId, friendId);
  }

  private UsersRequest usersRequest() {
    return UsersRequest.newBuilder()
        .setUsername(username)
        .build();
  }
}
