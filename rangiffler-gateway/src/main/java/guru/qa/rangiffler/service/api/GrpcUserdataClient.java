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
  private RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub stub;
  private final GrpcCall grpcCall = new GrpcCall();

  public UserResponse getCurrentUser(String username) {
    return grpcCall.execute(
        () -> stub.currentUser(
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
    return grpcCall.execute(() -> stub.updateUser(userRequest));
  }

  public UserPageResponse listUsers(UserPageRequest request) {
    return grpcCall.execute(() -> stub.listUsers(request));
  }

  public UserPageResponse listFriends(UserPageRequest request) {
    return grpcCall.execute(() -> stub.listFriends(request));
  }

  public UserResponse sendInvitation(String username, String targetUserId) {
    return grpcCall.execute(() ->
        stub.sendRequest(
            FriendshipRequest.newBuilder()
                .setRequester(username)
                .setAddressee(targetUserId)
                .build())
    );
  }

  public UserPageResponse listOutcomeInvitations(UserPageRequest request) {
    return grpcCall.execute(() -> stub.listOutcomeInvitations(request));
  }

  public UserPageResponse listIncomeInvitations(UserPageRequest request) {
    return grpcCall.execute(() -> stub.listIncomeInvitations(request));
  }

  public UserResponse acceptInvitation(String username, String targetUserId) {
    return grpcCall.execute(
        () -> stub.acceptRequest(
            FriendshipRequest.newBuilder()
                .setRequester(username)
                .setAddressee(targetUserId)
                .build())
    );
  }

  public UserResponse declineInvitation(String username, String targetUserId) {
    return grpcCall.execute(
        () -> stub.declineRequest(
            FriendshipRequest.newBuilder()
                .setRequester(username)
                .setAddressee(targetUserId)
                .build())
    );
  }

  public UserResponse removeFriend(String username, String targetUserId) {
    return grpcCall.execute(
        () -> stub.removeFriend(
            FriendshipRequest.newBuilder()
                .setRequester(username)
                .setAddressee(targetUserId)
                .build())
    );
  }
}
