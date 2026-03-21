package guru.qa.rangiffler.service;

import guru.qa.rangiffler.data.CountryValues;
import guru.qa.rangiffler.data.UserEntity;
import guru.qa.rangiffler.data.repository.UserRepository;
import guru.qa.rangiffler.grpc.CurrentUserRequest;
import guru.qa.rangiffler.grpc.FriendshipStatus;
import guru.qa.rangiffler.grpc.RangifflerUserdataServiceGrpc;
import guru.qa.rangiffler.grpc.UserPageRequest;
import guru.qa.rangiffler.grpc.UserPageResponse;
import guru.qa.rangiffler.grpc.UserRequest;
import guru.qa.rangiffler.grpc.UserResponse;
import guru.qa.rangiffler.util.ByteAsString;
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
  private final UserRepository userRepository;
  public final GrpcGeoClient grpcGeoClient;
  public static final CountryValues DEFAULT_COUNTRY = CountryValues.RU;

  @Autowired
  public GrpcUserService(UserService userService, UserRepository userRepository, GrpcGeoClient grpcGeoClient) {
    this.userService = userService;
    this.userRepository = userRepository;
    this.grpcGeoClient = grpcGeoClient;
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

  @Override
  @Transactional
  public void updateUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {
    UserEntity ue = userRepository.findByUsername(request.getUsername())
        .orElseGet(() -> {
              UserEntity emptyUser = new UserEntity();
              emptyUser.setUsername(request.getUsername());
              emptyUser.setCountry(DEFAULT_COUNTRY);
              return emptyUser;
            }
        );
    ue.setCountry(
        request.getCountry().isEmpty()
            ? DEFAULT_COUNTRY
            : CountryValues.valueOf(request.getCountry().toUpperCase())
    );
    ue.setAvatar(
        request.getAvatar().isEmpty()
            ? null
            : new SmallAvatar(220, 220, request.getAvatar()).bytes()
    );
    userRepository.save(ue);
    ue.setFirstname(request.getFirstname().isEmpty() ? null : request.getFirstname());
    ue.setSurname(request.getSurname().isEmpty() ? null : request.getSurname());
    responseObserver.onNext(userFromEntity(ue));
    responseObserver.onCompleted();
  }

  @Override
  public void listUsers(UserPageRequest request, StreamObserver<UserPageResponse> responseObserver) {
    Page<UserEntity> users = userService.allUsers(
        request.getUsername(),
        PageRequest.of(request.getPage(), request.getSize()),
        request.getSearchQuery()
    );
    responseObserver.onNext(setUserPageResponse(users));
    responseObserver.onCompleted();
  }

  private UserPageResponse setUserPageResponse(Page<UserEntity> users) {
    List<UserResponse> userResponses = users.getContent()
        .stream()
        .map(this::userFromEntity)
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

  private UserResponse userFromEntity(UserEntity ue) {
    return UserResponse.newBuilder()
        .setId(ue.getId().toString())
        .setUsername(ue.getUsername())
        .setFirstname(ue.getFirstname() == null ? "" : ue.getFirstname())
        .setSurname(ue.getSurname() == null ? "" : ue.getSurname())
        .setAvatar(new ByteAsString(ue.getAvatar()).string())
        .setCountry(guru.qa.rangiffler.grpc.CountryValues.valueOf(ue.getCountry().name()))
        .setFriendshipStatus(FriendshipStatus.UNSPECIFIED_STATUS)
        .build();
  }
}
