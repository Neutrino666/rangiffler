package guru.qa.rangiffler.service;

import guru.qa.rangiffler.data.UserEntity;
import guru.qa.rangiffler.data.repository.UserRepository;
import guru.qa.rangiffler.grpc.CurrentUserRequest;
import guru.qa.rangiffler.grpc.FriendshipStatus;
import guru.qa.rangiffler.grpc.RangifflerUserdataServiceGrpc;
import guru.qa.rangiffler.grpc.UserResponse;
import guru.qa.rangiffler.util.ByteAsString;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@GrpcService
public class GrpcUserService extends RangifflerUserdataServiceGrpc.RangifflerUserdataServiceImplBase {

  private final UserRepository userRepository;

  @Autowired
  public GrpcUserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public void currentUser(CurrentUserRequest request, StreamObserver<UserResponse> responseObserver) {
    UserResponse response = userRepository.findByUsername(request.getUsername())
            .map(this::userFromEntity)
                .orElse(UserResponse.newBuilder()
                    .setId("")
                    .setUsername(request.getUsername())
                    .build());
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  private UserResponse userFromEntity(UserEntity ue) {
    return UserResponse.newBuilder()
        .setId(ue.getId().toString())
        .setUsername(ue.getUsername())
        .setFirstname(ue.getFirstname() == null ? "" : ue.getFirstname())
        .setSurname(ue.getSurname() == null ? "" : ue.getSurname())
        .setAvatar(new ByteAsString(ue.getAvatar()).string())
        .setCountryId(ue.getCountryId().toString())
        .setFriendshipStatus(FriendshipStatus.UNSPECIFIED_STATUS)
        .build();
  }
}
