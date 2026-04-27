package guru.qa.rangiffler.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import guru.qa.rangiffler.data.CountryValues;
import guru.qa.rangiffler.grpc.CurrentUserRequest;
import guru.qa.rangiffler.grpc.FriendshipRequest;
import guru.qa.rangiffler.grpc.FriendshipStatus;
import guru.qa.rangiffler.grpc.UserPageRequest;
import guru.qa.rangiffler.grpc.UserPageResponse;
import guru.qa.rangiffler.grpc.UserRequest;
import guru.qa.rangiffler.grpc.UserResponse;
import guru.qa.rangiffler.grpc.UsersRequest;
import guru.qa.rangiffler.grpc.UsersResponse;
import guru.qa.rangiffler.model.UserJson;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ParametersAreNonnullByDefault
@ExtendWith(MockitoExtension.class)
public class GrpcUserServiceTest {

  @Mock
  private UserService userService;

  @Mock
  private StreamObserver<UserResponse> userResponseStreamObserver;

  @Mock
  private StreamObserver<UsersResponse> usersResponseStreamObserver;

  @Mock
  private StreamObserver<UserPageResponse> userPageResponseStreamObserver;

  private GrpcUserService grpcUserService;

  @Captor
  private ArgumentCaptor<UserResponse> userResponseCaptor;

  @Captor
  private ArgumentCaptor<UsersResponse> usersResponseCaptor;

  @Captor
  private ArgumentCaptor<UserPageResponse> userPageResponseCaptor;

  private final UserJson user = new UserJson(
      UUID.randomUUID(),
      "test",
      "firstname",
      "surname",
      CountryValues.AD,
      "123",
      null
  );

  final FriendshipRequest friendshipRequest = FriendshipRequest.newBuilder()
      .setRequester("test")
      .setAddressee(UUID.randomUUID().toString())
      .build();

  final UserPageRequest userPageRequest = UserPageRequest.newBuilder()
      .setUsername(user.username())
      .setSize(1)
      .setPage(1)
      .build();

  @BeforeEach
  void before() {
    grpcUserService = new GrpcUserService(this.userService);
  }

  @Test
  void currentUserShouldCallUserService() {
    final UserResponse expected = userResponseFromUserJson(user);
    when(userService.getCurrentUser(eq(user.username()))).thenReturn(user);
    CurrentUserRequest request = CurrentUserRequest.newBuilder()
        .setUsername(user.username()).build();
    grpcUserService.currentUser(request, userResponseStreamObserver);

    verify(userService).getCurrentUser(user.username());
    verify(userResponseStreamObserver).onNext(userResponseCaptor.capture());
    verify(userResponseStreamObserver).onCompleted();
    final UserResponse actual = userResponseCaptor.getValue();
    Assertions.assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  void updateUserShouldCallUserService() {
    final UserResponse expected = userResponseFromUserJson(user);
    final UserRequest request = UserRequest.newBuilder().build();
    when(userService.update(eq(request))).thenReturn(user);
    grpcUserService.updateUser(request, userResponseStreamObserver);

    verify(userService).update(request);
    verify(userResponseStreamObserver).onNext(userResponseCaptor.capture());
    verify(userResponseStreamObserver).onCompleted();
    final UserResponse actual = userResponseCaptor.getValue();
    Assertions.assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  void listUsersShouldCallUserService() {
    Page<UserJson> userPage = new PageImpl<>(List.of(user));
    final UserPageResponse expected = userPageResponseFromPageUserJson(userPage);
    when(userService.allUsers(
        eq(userPageRequest.getUsername()),
        eq(PageRequest.of(userPageRequest.getPage(), userPageRequest.getSize())),
        eq(userPageRequest.getSearchQuery()))
    ).thenReturn(userPage);
    grpcUserService.listUsers(userPageRequest, userPageResponseStreamObserver);

    verify(userService).allUsers(
        eq(userPageRequest.getUsername()),
        eq(PageRequest.of(userPageRequest.getPage(), userPageRequest.getSize())),
        eq(userPageRequest.getSearchQuery()));
    verify(userPageResponseStreamObserver).onNext(userPageResponseCaptor.capture());
    verify(userPageResponseStreamObserver).onCompleted();
    final UserPageResponse actual = userPageResponseCaptor.getValue();
    Assertions.assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  void listFriendsShouldCallUserService() {
    final UserPageRequest request = UserPageRequest.newBuilder()
        .setUsername(user.username())
        .setSize(1)
        .setPage(1)
        .build();
    Page<UserJson> userPage = new PageImpl<>(List.of(user));
    final UserPageResponse expected = userPageResponseFromPageUserJson(userPage);
    when(userService.friends(
        eq(request.getUsername()),
        eq(PageRequest.of(request.getPage(), request.getSize())),
        eq(request.getSearchQuery()))
    ).thenReturn(userPage);
    grpcUserService.listFriends(request, userPageResponseStreamObserver);

    verify(userService).friends(
        eq(request.getUsername()),
        eq(PageRequest.of(request.getPage(), request.getSize())),
        eq(request.getSearchQuery()));
    verify(userPageResponseStreamObserver).onNext(userPageResponseCaptor.capture());
    verify(userPageResponseStreamObserver).onCompleted();
    final UserPageResponse actual = userPageResponseCaptor.getValue();
    Assertions.assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  void listFriendIdsShouldCallUserService() {
    final UsersRequest request = UsersRequest.newBuilder()
        .setUsername("test")
        .build();
    final List<UUID> uuids = List.of(UUID.randomUUID());
    final UsersResponse expected = UsersResponse
        .newBuilder()
        .addAllId(uuids.stream().map(UUID::toString).toList())
        .build();
    when(userService.friendsIds(eq(request.getUsername())))
        .thenReturn(uuids.stream());
    grpcUserService.listFriendsIds(request, usersResponseStreamObserver);

    verify(userService).friendsIds(request.getUsername());
    verify(usersResponseStreamObserver).onNext(usersResponseCaptor.capture());
    verify(usersResponseStreamObserver).onCompleted();
    final UsersResponse actual = usersResponseCaptor.getValue();
    Assertions.assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  void sendRequestShouldCallUserService() {
    when(
        userService.createFriendshipRequest(eq(friendshipRequest.getRequester()), eq(friendshipRequest.getAddressee())))
        .thenReturn(user);
    grpcUserService.sendRequest(friendshipRequest, userResponseStreamObserver);

    verify(userService).createFriendshipRequest(friendshipRequest.getRequester(), friendshipRequest.getAddressee());
    verify(userResponseStreamObserver).onNext(userResponseCaptor.capture());
    verify(userResponseStreamObserver).onCompleted();
    final UserResponse actual = userResponseCaptor.getValue();
    Assertions.assertThat(actual)
        .isEqualTo(userResponseFromUserJson(user));
  }

  @Test
  void acceptRequestShouldCallUserService() {
    when(
        userService.acceptFriendshipRequest(eq(friendshipRequest.getRequester()), eq(friendshipRequest.getAddressee())))
        .thenReturn(user);
    grpcUserService.acceptRequest(friendshipRequest, userResponseStreamObserver);

    verify(userService).acceptFriendshipRequest(friendshipRequest.getRequester(), friendshipRequest.getAddressee());
    verify(userResponseStreamObserver).onNext(userResponseCaptor.capture());
    verify(userResponseStreamObserver).onCompleted();
    final UserResponse actual = userResponseCaptor.getValue();
    Assertions.assertThat(actual)
        .isEqualTo(userResponseFromUserJson(user));
  }

  @Test
  void declineRequestShouldCallUserService() {
    when(userService.declineFriendshipRequest(eq(friendshipRequest.getRequester()),
        eq(friendshipRequest.getAddressee())))
        .thenReturn(user);
    grpcUserService.declineRequest(friendshipRequest, userResponseStreamObserver);

    verify(userService).declineFriendshipRequest(friendshipRequest.getRequester(), friendshipRequest.getAddressee());
    verify(userResponseStreamObserver).onNext(userResponseCaptor.capture());
    verify(userResponseStreamObserver).onCompleted();
    final UserResponse actual = userResponseCaptor.getValue();
    Assertions.assertThat(actual)
        .isEqualTo(userResponseFromUserJson(user));
  }

  @Test
  void removeFriendShouldCallUserService() {
    when(userService.removeFriend(eq(friendshipRequest.getRequester()), eq(friendshipRequest.getAddressee())))
        .thenReturn(user);
    grpcUserService.removeFriend(friendshipRequest, userResponseStreamObserver);

    verify(userService).removeFriend(friendshipRequest.getRequester(), friendshipRequest.getAddressee());
    verify(userResponseStreamObserver).onNext(userResponseCaptor.capture());
    verify(userResponseStreamObserver).onCompleted();
    final UserResponse actual = userResponseCaptor.getValue();
    Assertions.assertThat(actual)
        .isEqualTo(userResponseFromUserJson(user));
  }

  @Test
  void listOutcomeInvitationsShouldCallUserService() {
    Page<UserJson> userPage = new PageImpl<>(List.of(user));
    final UserPageResponse expected = userPageResponseFromPageUserJson(userPage);
    when(userService.outcomeInvitations(
        eq(userPageRequest.getUsername()),
        eq(PageRequest.of(userPageRequest.getPage(), userPageRequest.getSize())),
        eq(userPageRequest.getSearchQuery()))
    ).thenReturn(userPage);
    grpcUserService.listOutcomeInvitations(userPageRequest, userPageResponseStreamObserver);

    verify(userService).outcomeInvitations(
        eq(userPageRequest.getUsername()),
        eq(PageRequest.of(userPageRequest.getPage(), userPageRequest.getSize())),
        eq(userPageRequest.getSearchQuery()));
    verify(userPageResponseStreamObserver).onNext(userPageResponseCaptor.capture());
    verify(userPageResponseStreamObserver).onCompleted();
    final UserPageResponse actual = userPageResponseCaptor.getValue();
    Assertions.assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  void listIncomeInvitationsShouldCallUserService() {
    Page<UserJson> userPage = new PageImpl<>(List.of(user));
    final UserPageResponse expected = userPageResponseFromPageUserJson(userPage);
    when(userService.incomeInvitations(
        eq(userPageRequest.getUsername()),
        eq(PageRequest.of(userPageRequest.getPage(), userPageRequest.getSize())),
        eq(userPageRequest.getSearchQuery()))
    ).thenReturn(userPage);
    grpcUserService.listIncomeInvitations(userPageRequest, userPageResponseStreamObserver);

    verify(userService).incomeInvitations(
        eq(userPageRequest.getUsername()),
        eq(PageRequest.of(userPageRequest.getPage(), userPageRequest.getSize())),
        eq(userPageRequest.getSearchQuery()));
    verify(userPageResponseStreamObserver).onNext(userPageResponseCaptor.capture());
    verify(userPageResponseStreamObserver).onCompleted();
    final UserPageResponse actual = userPageResponseCaptor.getValue();
    Assertions.assertThat(actual)
        .isEqualTo(expected);
  }

  private @Nonnull UserResponse userResponseFromUserJson(final UserJson user) {
    return UserResponse.newBuilder()
        .setId(user.id() == null ? "" : user.id().toString())
        .setUsername(user.username())
        .setFirstname(user.firstname() == null ? "" : user.firstname())
        .setSurname(user.surname() == null ? "" : user.surname())
        .setAvatar(user.avatar())
        .setCountry(guru.qa.rangiffler.grpc.CountryValues.valueOf(user.country().name()))
        .setFriendshipStatus(user.friendshipStatus() == null
            ? guru.qa.rangiffler.grpc.FriendshipStatus.NOT_FRIEND
            : FriendshipStatus.valueOf(user.friendshipStatus().name()))
        .build();
  }

  private @Nonnull UserPageResponse userPageResponseFromPageUserJson(final Page<UserJson> users) {
    final List<UserResponse> userResponses = users.getContent()
        .stream()
        .map(this::userResponseFromUserJson)
        .toList();
    return UserPageResponse.newBuilder()
        .setTotalElements(users.getTotalElements())
        .setTotalPages(users.getTotalPages())
        .setFirst(users.isFirst())
        .setLast(users.isLast())
        .setSize(users.getSize())
        .addAllEdges(userResponses)
        .build();
  }
}
