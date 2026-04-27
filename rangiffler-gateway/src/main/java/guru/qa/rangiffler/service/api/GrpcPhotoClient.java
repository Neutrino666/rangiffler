package guru.qa.rangiffler.service.api;

import guru.qa.rangiffler.grpc.CountryRequest;
import guru.qa.rangiffler.grpc.FeedRequest;
import guru.qa.rangiffler.grpc.LikeRequest;
import guru.qa.rangiffler.grpc.PhotoDeleteRequest;
import guru.qa.rangiffler.grpc.PhotoPageResponse;
import guru.qa.rangiffler.grpc.PhotoRequest;
import guru.qa.rangiffler.grpc.PhotoResponse;
import guru.qa.rangiffler.grpc.RangifflerPhotoServiceGrpc;
import guru.qa.rangiffler.service.utils.GrpcCall;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import rangiffler.graphqlTypes.PhotoInput;

@Slf4j
@Component
@ParametersAreNonnullByDefault
public class GrpcPhotoClient {

  @GrpcClient("grpcPhotoClient")
  private RangifflerPhotoServiceGrpc.RangifflerPhotoServiceBlockingStub stub;
  private final GrpcCall grpcCall = new GrpcCall();

  public PhotoResponse addPhoto(PhotoInput photo, final UUID userId) {
    return grpcCall.execute(() ->
        stub.createPhoto(
            PhotoRequest.newBuilder()
                .setSrc(photo.getSrc())
                .setUserId(userId.toString())
                .setCountry(
                    CountryRequest.newBuilder()
                        .setCode(photo.getCountry().getCode())
                        .build()
                )
                .setDescription(photo.getDescription())
                .build())
    );
  }

  public PhotoResponse updatePhoto(final PhotoInput photo, final UUID userId) {
    return grpcCall.execute(() ->
        stub.updatePhoto(PhotoRequest.newBuilder()
            .setId(photo.getId())
            .setUserId(userId.toString())
            .setSrc(photo.getSrc())
            .setCountry(
                CountryRequest.newBuilder()
                    .setCode(photo.getCountry().getCode())
                    .build()
            )
            .setDescription(photo.getDescription())
            .build())
    );
  }

  public PhotoResponse updateLike(final PhotoInput input, final UUID userId, String username) {
    return grpcCall.execute(() ->
        stub.photoLike(
            LikeRequest.newBuilder()
                .setUserId(userId.toString())
                .setUsername(username)
                .setPhotoId(input.getId())
                .setRequesterId(input.getLike().getUser())
                .build()
        ));
  }

  public Boolean deletePhoto(final UUID id, final UUID userId) {
    return grpcCall.execute(() ->
        stub.deletePhoto(PhotoDeleteRequest.newBuilder()
            .setId(id.toString())
            .setUserId(userId.toString())
            .build())
    ).getIsDeleted();
  }

  public PhotoPageResponse listPhoto(FeedRequest request) {
    return grpcCall.execute(() ->
        stub.listPhoto(request)
    );
  }
}
