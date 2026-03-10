package guru.qa.rangiffler.service.api;

import guru.qa.rangiffler.grpc.PhotoRequest;
import guru.qa.rangiffler.grpc.PhotoResponse;
import guru.qa.rangiffler.grpc.RangifflerPhotoServiceGrpc;
import io.grpc.StatusRuntimeException;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import rangiffler.graphqlTypes.PhotoInput;

@Slf4j
@Component
@ParametersAreNonnullByDefault
public class GrpcPhotoClient {

  @GrpcClient("grpcPhotoClient")
  private RangifflerPhotoServiceGrpc.RangifflerPhotoServiceBlockingStub rangifflerPhotoServiceBlockingStub;

  public PhotoResponse updatePhoto(PhotoInput in) {
    try {
      return rangifflerPhotoServiceBlockingStub.updatePhoto(PhotoRequest.newBuilder()
          .setDescription("Котик111111")
          .setCountry("af")
          .setUser("dididididi")
          .setSrc(in.getSrc())
          .build());
    } catch (StatusRuntimeException e) {
      log.error("### Error while calling gRPC server ", e);
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
    }
  }
}
