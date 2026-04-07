package guru.qa.rangiffler.service.api;

import guru.qa.rangiffler.grpc.RangifflerPhotoServiceGrpc;
import guru.qa.rangiffler.grpc.StatRequest;
import guru.qa.rangiffler.grpc.StatResponse;
import guru.qa.rangiffler.service.utils.GrpcCall;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
@ParametersAreNonnullByDefault
@NoArgsConstructor(access = AccessLevel.NONE)
public final class GrpcStatClient {

  @GrpcClient("grpcPhotoClient")
  private RangifflerPhotoServiceGrpc.RangifflerPhotoServiceBlockingStub rangifflerPhotoServiceBlockingStub;

  public StatResponse stat(final StatRequest request) {
    return GrpcCall.execute(() ->
        rangifflerPhotoServiceBlockingStub.stat(request)
    );
  }
}
