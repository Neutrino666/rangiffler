package guru.qa.rangiffler.service;

import guru.qa.rangiffler.grpc.CurrentUserRequest;
import guru.qa.rangiffler.grpc.FriendshipStatus;
import guru.qa.rangiffler.grpc.RangifflerUserdataServiceGrpc;
import guru.qa.rangiffler.grpc.UserResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class GrpcUserService extends RangifflerUserdataServiceGrpc.RangifflerUserdataServiceImplBase {

  @Autowired
  public GrpcUserService() {}

  @Override
  public void currentUser(CurrentUserRequest request, StreamObserver<UserResponse> responseObserver) {
    responseObserver.onNext(setUserResponse());
    responseObserver.onCompleted();
  }

  private UserResponse setUserResponse() {
    return UserResponse.newBuilder()
        .setId("1ba62f37-a3da-4e4f-ac99-40d83daa099c")
        .setUsername("Tuzik")
        .setFirstname(("Dog"))
        .setSurname("Будкин")
        .setAvatar("")
        .setFriendshipStatus(FriendshipStatus.INVITE_SENT)
        .build();
  }
}
