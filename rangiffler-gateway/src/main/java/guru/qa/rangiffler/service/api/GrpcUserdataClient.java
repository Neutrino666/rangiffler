package guru.qa.rangiffler.service.api;

import guru.qa.rangiffler.grpc.CurrentUserRequest;
import guru.qa.rangiffler.grpc.RangifflerUserdataServiceGrpc;
import guru.qa.rangiffler.grpc.UserPageRequest;
import guru.qa.rangiffler.grpc.UserPageResponse;
import guru.qa.rangiffler.grpc.UserRequest;
import guru.qa.rangiffler.grpc.UserResponse;
import io.grpc.StatusRuntimeException;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
@ParametersAreNonnullByDefault
public class GrpcUserdataClient {

  @GrpcClient("grpcUserdataClient")
  private RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub rangifflerUserdataServiceStub;

  public UserResponse getCurrentUser(String username) {
    try {
      return rangifflerUserdataServiceStub.currentUser(
          CurrentUserRequest.newBuilder()
              .setUsername(username)
              .build()
      );
    } catch (StatusRuntimeException e) {
      log.error("### Error while calling gRPC server ", e);
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
    }
  }

  public UserResponse updateUser(UserRequest userRequest) {
    try {
      return rangifflerUserdataServiceStub.updateUser(userRequest);
    } catch (StatusRuntimeException e) {
      log.error("### Error while calling gRPC server ", e);
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
    }
  }

  public UserPageResponse listUsers(UserPageRequest request) {
    try {
      return rangifflerUserdataServiceStub.listUsers(request);
    } catch (StatusRuntimeException e) {
      log.error("### Error while calling gRPC server ", e);
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
    }
  }
}
