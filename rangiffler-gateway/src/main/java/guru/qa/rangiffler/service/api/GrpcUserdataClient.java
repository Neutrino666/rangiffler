package guru.qa.rangiffler.service.api;

import guru.qa.rangiffler.grpc.CurrentUserRequest;
import guru.qa.rangiffler.grpc.FriendshipRequest;
import guru.qa.rangiffler.grpc.RangifflerUserdataServiceGrpc;
import guru.qa.rangiffler.grpc.UserPageRequest;
import guru.qa.rangiffler.grpc.UserPageResponse;
import guru.qa.rangiffler.grpc.UserRequest;
import guru.qa.rangiffler.grpc.UserResponse;
import guru.qa.rangiffler.service.utils.GrpcCall;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@ParametersAreNonnullByDefault
public class GrpcUserdataClient {

  @GrpcClient("grpcUserdataClient")
  private RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub rangifflerUserdataServiceStub;

  public UserResponse getCurrentUser(String username) {
    return GrpcCall.execute(
        () -> rangifflerUserdataServiceStub.currentUser(
            CurrentUserRequest.newBuilder()
                .setUsername(username)
                .build()
        )
    );
  }

  @Cacheable(value = "getCurrentUserId", key = "#username")
  public UUID getCurrentUserId(String username) {
    return UUID.fromString(getCurrentUser(username).getId());
  }

  public UserResponse updateUser(UserRequest userRequest) {
    return GrpcCall.execute(() -> rangifflerUserdataServiceStub.updateUser(userRequest));
  }

  public UserPageResponse listUsers(UserPageRequest request) {
    return GrpcCall.execute(() -> rangifflerUserdataServiceStub.listUsers(request));
  }

  public UserPageResponse listFriends(UserPageRequest request) {
    return GrpcCall.execute(() -> rangifflerUserdataServiceStub.listFriends(request));
  }

  public UserResponse sendInvitation(String username, String targetUserId) {
    return GrpcCall.execute(() ->
        rangifflerUserdataServiceStub.sendRequest(
            FriendshipRequest.newBuilder()
                .setRequester(username)
                .setAddressee(targetUserId)
                .build())
    );
  }

  public UserPageResponse listOutcomeInvitations(UserPageRequest request) {
    return GrpcCall.execute(() -> rangifflerUserdataServiceStub.listOutcomeInvitations(request));
  }

  public UserPageResponse listIncomeInvitations(UserPageRequest request) {
    return GrpcCall.execute(() -> rangifflerUserdataServiceStub.listIncomeInvitations(request));
  }

  public UserResponse acceptInvitation(String username, String targetUserId) {
    return GrpcCall.execute(
        () -> rangifflerUserdataServiceStub.acceptRequest(
            FriendshipRequest.newBuilder()
                .setRequester(username)
                .setAddressee(targetUserId)
                .build())
    );
  }

  public UserResponse declineInvitation(String username, String targetUserId) {
    return GrpcCall.execute(
        () -> rangifflerUserdataServiceStub.declineRequest(
            FriendshipRequest.newBuilder()
                .setRequester(username)
                .setAddressee(targetUserId)
                .build())
    );
  }

  public UserResponse removeFriend(String username, String targetUserId) {
    return GrpcCall.execute(
        () -> rangifflerUserdataServiceStub.removeFriend(
            FriendshipRequest.newBuilder()
                .setRequester(username)
                .setAddressee(targetUserId)
                .build())
    );
  }
}
