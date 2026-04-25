package guru.qa.rangiffler.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import guru.qa.rangiffler.grpc.RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub;
import guru.qa.rangiffler.service.api.GrpcUserdataClient;
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
public class GrpcUserdataClientTest {

  private final String grpcCallErrorMessage = "503 SERVICE_UNAVAILABLE \"The gRPC operation was cancelled\"";

  private final RangifflerUserdataServiceBlockingStub stub = mock(RangifflerUserdataServiceBlockingStub.class);
  private final GrpcUserdataClient grpcUserdataClient = new GrpcUserdataClient();
  private final UUID userId = UUID.randomUUID();
  private final String username = "test";
  private UserResponse user;
  private UserPageRequest userPageRequest;
  private UserPageResponse userPageResponse;

  @BeforeEach
  void before() {
    ReflectionTestUtils.setField(grpcUserdataClient, "stub", stub);
    user = UserResponse.newBuilder()
        .setId(userId.toString())
        .setUsername(username)
        .setFirstname("firstName")
        .setSurname("surname")
        .setAvatar("")
        .setCountry(CountryValues.AD)
        .setFriendshipStatus(FriendshipStatus.FRIEND)
        .build();

    userPageRequest = UserPageRequest.newBuilder()
        .setPage(0)
        .setSize(10)
        .setSearchQuery("")
        .setUsername(username)
        .build();

    userPageResponse = UserPageResponse.newBuilder()
        .setTotalElements(1)
        .setTotalPages(1)
        .setFirst(true)
        .setLast(true)
        .setSize(1)
        .addAllEdges(List.of(user))
        .build();
  }

  @Test
  void currentUserShouldReturnUserResponse() {
    final CurrentUserRequest userRequest = CurrentUserRequest.newBuilder()
        .setUsername(username)
        .build();
    when(stub.currentUser(userRequest)).thenReturn(user);
    final UserResponse actual = grpcUserdataClient.getCurrentUser(username);

    verify(stub).currentUser(userRequest);
    assertThat(actual).isEqualTo(user);
  }

  @Test
  void currentUserShouldReturnResponseStatusException() {
    when(stub.currentUser(any(CurrentUserRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));
    assertThatThrownBy(() -> grpcUserdataClient.getCurrentUser(username))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessage(grpcCallErrorMessage)
        .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
        .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);

  }

  @Test
  void currentUserIdShouldReturnId() {
    final CurrentUserRequest userRequest = CurrentUserRequest.newBuilder()
        .setUsername(username)
        .build();
    when(stub.currentUser(userRequest)).thenReturn(user);

    final UUID actual = grpcUserdataClient.getCurrentUserId(username);
    assertThat(actual).isEqualTo(userId);
  }

  @Test
  void currentUserIdShouldResponseStatusException() {
    when(stub.currentUser(any(CurrentUserRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));
    assertThatThrownBy(() -> grpcUserdataClient.getCurrentUserId(username))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessage(grpcCallErrorMessage)
        .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
        .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
  }

  @Test
  void updateUserShouldReturnUserResponse() {
    when(stub.updateUser(any(UserRequest.class))).thenReturn(user);
    final UserResponse actual = grpcUserdataClient.updateUser(UserRequest.newBuilder().build());

    verify(stub).updateUser(UserRequest.newBuilder().build());
    assertThat(actual).isEqualTo(user);
  }

  @Test
  void updateUserShouldResponseStatusException() {
    when(stub.updateUser(any(UserRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));
    assertThatThrownBy(() -> grpcUserdataClient.updateUser(UserRequest.newBuilder().build()))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessage(grpcCallErrorMessage)
        .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
        .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
  }

  @Test
  void ListUsersShouldReturnUserPageRequest() {
    when(stub.listUsers(userPageRequest)).thenReturn(userPageResponse);
    final UserPageResponse actual = grpcUserdataClient.listUsers(userPageRequest);

    verify(stub).listUsers(userPageRequest);
    assertThat(actual).isEqualTo(userPageResponse);
  }

  @Test
  void ListUsersShouldResponseStatusException() {
    when(stub.listUsers(any(UserPageRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));
    assertThatThrownBy(() -> grpcUserdataClient.listUsers(UserPageRequest.newBuilder().build()))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessage(grpcCallErrorMessage)
        .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
        .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
  }

  @Test
  void listFriendsShouldReturnUserPageRequest() {
    when(stub.listFriends(userPageRequest)).thenReturn(userPageResponse);
    final UserPageResponse actual = grpcUserdataClient.listFriends(userPageRequest);

    verify(stub).listFriends(userPageRequest);
    assertThat(actual).isEqualTo(userPageResponse);
  }

  @Test
  void listFriendsShouldResponseStatusException() {
    when(stub.listFriends(any(UserPageRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));
    assertThatThrownBy(() -> grpcUserdataClient.listFriends(UserPageRequest.newBuilder().build()))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessage(grpcCallErrorMessage)
        .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
        .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
  }

  @Test
  void listOutcomeInvitationsShouldReturnUserPageRequest() {
    when(stub.listOutcomeInvitations(userPageRequest)).thenReturn(userPageResponse);
    final UserPageResponse actual = grpcUserdataClient.listOutcomeInvitations(userPageRequest);

    verify(stub).listOutcomeInvitations(userPageRequest);
    assertThat(actual).isEqualTo(userPageResponse);
  }

  @Test
  void listOutcomeInvitationsShouldResponseStatusException() {
    when(stub.listOutcomeInvitations(any(UserPageRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));
    assertThatThrownBy(() -> grpcUserdataClient.listOutcomeInvitations(UserPageRequest.newBuilder().build()))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessage(grpcCallErrorMessage)
        .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
        .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
  }

  @Test
  void listIncomeInvitationsShouldReturnUserPageRequest() {
    when(stub.listIncomeInvitations(userPageRequest)).thenReturn(userPageResponse);
    final UserPageResponse actual = grpcUserdataClient.listIncomeInvitations(userPageRequest);

    verify(stub).listIncomeInvitations(userPageRequest);
    assertThat(actual).isEqualTo(userPageResponse);
  }

  @Test
  void listIncomeInvitationsShouldResponseStatusException() {
    when(stub.listIncomeInvitations(any(UserPageRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));
    assertThatThrownBy(() -> grpcUserdataClient.listIncomeInvitations(UserPageRequest.newBuilder().build()))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessage(grpcCallErrorMessage)
        .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
        .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
  }

  @Test
  void sendInvitationShouldReturnUserResponse() {
    final UUID targetUserId = UUID.randomUUID();
    final FriendshipRequest request = FriendshipRequest.newBuilder()
        .setRequester(username)
        .setAddressee(targetUserId.toString())
        .build();
    when(stub.sendRequest(request)).thenReturn(user);
    final UserResponse actual = grpcUserdataClient.sendInvitation(username, targetUserId.toString());

    verify(stub).sendRequest(request);
    assertThat(actual).isEqualTo(user);
  }

  @Test
  void sendInvitationShouldResponseStatusException() {
    when(stub.sendRequest(any(FriendshipRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));
    assertThatThrownBy(() -> grpcUserdataClient.sendInvitation(username, userId.toString()))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessage(grpcCallErrorMessage)
        .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
        .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
  }

  @Test
  void acceptInvitationShouldReturnUserResponse() {
    final UUID targetUserId = UUID.randomUUID();
    final FriendshipRequest request = FriendshipRequest.newBuilder()
        .setRequester(username)
        .setAddressee(targetUserId.toString())
        .build();
    when(stub.acceptRequest(request)).thenReturn(user);
    final UserResponse actual = grpcUserdataClient.acceptInvitation(username, targetUserId.toString());

    verify(stub).acceptRequest(request);
    assertThat(actual).isEqualTo(user);
  }

  @Test
  void acceptInvitationShouldResponseStatusException() {
    when(stub.acceptRequest(any(FriendshipRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));
    assertThatThrownBy(() -> grpcUserdataClient.acceptInvitation(username, userId.toString()))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessage(grpcCallErrorMessage)
        .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
        .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
  }

  @Test
  void declineInvitationShouldReturnUserResponse() {
    final UUID targetUserId = UUID.randomUUID();
    final FriendshipRequest request = FriendshipRequest.newBuilder()
        .setRequester(username)
        .setAddressee(targetUserId.toString())
        .build();
    when(stub.declineRequest(request)).thenReturn(user);
    final UserResponse actual = grpcUserdataClient.declineInvitation(username, targetUserId.toString());

    verify(stub).declineRequest(request);
    assertThat(actual).isEqualTo(user);
  }

  @Test
  void declineInvitationShouldResponseStatusException() {
    when(stub.declineRequest(any(FriendshipRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));
    assertThatThrownBy(() -> grpcUserdataClient.declineInvitation(username, userId.toString()))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessage(grpcCallErrorMessage)
        .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
        .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
  }

  @Test
  void removeFriendShouldReturnUserResponse() {
    final UUID targetUserId = UUID.randomUUID();
    final FriendshipRequest request = FriendshipRequest.newBuilder()
        .setRequester(username)
        .setAddressee(targetUserId.toString())
        .build();
    when(stub.removeFriend(request)).thenReturn(user);
    final UserResponse actual = grpcUserdataClient.removeFriend(username, targetUserId.toString());

    verify(stub).removeFriend(request);
    assertThat(actual).isEqualTo(user);
  }

  @Test
  void removeFriendShouldResponseStatusException() {
    final UUID targetUserId = UUID.randomUUID();
    final FriendshipRequest request = FriendshipRequest.newBuilder()
        .setRequester(username)
        .setAddressee(targetUserId.toString())
        .build();
    when(stub.removeFriend(request)).thenReturn(user);
    final UserResponse actual = grpcUserdataClient.removeFriend(username, targetUserId.toString());

    verify(stub).removeFriend(request);
    assertThat(actual).isEqualTo(user);
  }
}
