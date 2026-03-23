package guru.qa.rangiffler.service;

import com.google.protobuf.Empty;
import guru.qa.rangiffler.grpc.CurrentUserRequest;
import guru.qa.rangiffler.grpc.FriendshipRequest;
import guru.qa.rangiffler.grpc.FriendshipStatus;
import guru.qa.rangiffler.grpc.RangifflerUserdataServiceGrpc;
import guru.qa.rangiffler.grpc.UserPageRequest;
import guru.qa.rangiffler.grpc.UserPageResponse;
import guru.qa.rangiffler.grpc.UserRequest;
import guru.qa.rangiffler.grpc.UserResponse;
import guru.qa.rangiffler.model.UserJson;
import io.grpc.stub.StreamObserver;
import java.util.List;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@GrpcService
public class GrpcUserService extends RangifflerUserdataServiceGrpc.RangifflerUserdataServiceImplBase {

  private final UserService userService;
  public final GrpcGeoClient grpcGeoClient;

  @Autowired
  public GrpcUserService(UserService userService, GrpcGeoClient grpcGeoClient) {
    this.userService = userService;
    this.grpcGeoClient = grpcGeoClient;
  }

  @Override
  @Transactional(readOnly = true)
  public void currentUser(CurrentUserRequest request, StreamObserver<UserResponse> responseObserver) {
    UserResponse response = setUserResponse(
        userService.getCurrentUser(request.getUsername())
    );
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  @Transactional
  public void updateUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {
    UserJson user = userService.update(request);
    responseObserver.onNext(setUserResponse(user));
    responseObserver.onCompleted();
  }

  @Override
  public void listUsers(UserPageRequest request, StreamObserver<UserPageResponse> responseObserver) {
    Page<UserJson> users = userService.allUsers(
        request.getUsername(),
        PageRequest.of(request.getPage(), request.getSize()),
        request.getSearchQuery()
    );
    responseObserver.onNext(setUserPageResponse(users));
    responseObserver.onCompleted();
  }

  @Override
  public void listFriends(UserPageRequest request, StreamObserver<UserPageResponse> responseObserver) {
    super.listFriends(request, responseObserver);
  }

  @Override
  public void sendRequest(FriendshipRequest request,
      StreamObserver<UserResponse> responseObserver) {
    UserJson user = userService.createFriendshipRequest(request.getRequester(),
        request.getAddressee());
    responseObserver.onNext(setUserResponse(user));
    responseObserver.onCompleted();
  }

  @Override
  public void acceptRequest(FriendshipRequest request, StreamObserver<UserResponse> responseObserver) {
    super.acceptRequest(request, responseObserver);
  }

  @Override
  public void declineRequest(FriendshipRequest request, StreamObserver<UserResponse> responseObserver) {
    super.declineRequest(request, responseObserver);
  }

  @Override
  public void removeFriend(FriendshipRequest request, StreamObserver<Empty> responseObserver) {
    super.removeFriend(request, responseObserver);
  }

  @Override
  public void listOutcomeInvitations(UserPageRequest request, StreamObserver<UserPageResponse> responseObserver) {
    Page<UserJson> outcomeInvitations = userService.outcomeInvitations(
        request.getUsername(),
        PageRequest.of(request.getPage(), request.getSize()),
        request.getSearchQuery()
    );
    responseObserver.onNext(setUserPageResponse(outcomeInvitations));
    responseObserver.onCompleted();
  }

  @Override
  public void listIncomeInvitations(UserPageRequest request, StreamObserver<UserPageResponse> responseObserver) {
    super.listIncomeInvitations(request, responseObserver);
  }

  private UserPageResponse setUserPageResponse(Page<UserJson> users) {
    List<UserResponse> userResponses = users.getContent()
        .stream()
        .map(this::setUserResponse)
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

  private UserResponse setUserResponse(UserJson user) {
    return UserResponse.newBuilder()
        .setId(user.id() == null ? "" : user.id().toString())
        .setUsername(user.username())
        .setFirstname(user.firstname() == null ? "" : user.firstname())
        .setSurname(user.surname() == null ? "" : user.surname())
        .setAvatar(user.avatar())
        .setCountry(guru.qa.rangiffler.grpc.CountryValues.valueOf(user.country().name()))
        .setFriendshipStatus(user.friendshipStatus() == null
            ? FriendshipStatus.NOT_FRIEND
            : FriendshipStatus.valueOf(user.friendshipStatus().name()))
        .build();
  }
}
