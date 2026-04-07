package guru.qa.rangiffler.service;

import com.google.protobuf.util.Timestamps;
import guru.qa.rangiffler.grpc.CountryStat;
import guru.qa.rangiffler.grpc.FeedRequest;
import guru.qa.rangiffler.grpc.Like;
import guru.qa.rangiffler.grpc.LikeRequest;
import guru.qa.rangiffler.grpc.PhotoDeleteRequest;
import guru.qa.rangiffler.grpc.PhotoDeleteResponse;
import guru.qa.rangiffler.grpc.PhotoPageResponse;
import guru.qa.rangiffler.grpc.PhotoRequest;
import guru.qa.rangiffler.grpc.PhotoResponse;
import guru.qa.rangiffler.grpc.RangifflerPhotoServiceGrpc;
import guru.qa.rangiffler.grpc.StatRequest;
import guru.qa.rangiffler.grpc.StatResponse;
import guru.qa.rangiffler.model.PhotoJson;
import guru.qa.rangiffler.model.StatisticJson;
import io.grpc.stub.StreamObserver;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@GrpcService
@ParametersAreNonnullByDefault
@NoArgsConstructor(access = AccessLevel.NONE)
public class GrpcPhotoService extends RangifflerPhotoServiceGrpc.RangifflerPhotoServiceImplBase {

  private final PhotoService photoService;

  private final StatService statService;

  @Autowired
  public GrpcPhotoService(PhotoService photoService,
      StatService statService) {
    this.photoService = photoService;
    this.statService = statService;
  }

  @Override
  public void createPhoto(PhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
    final PhotoJson photo = photoService.save(request);
    responseObserver.onNext(setPhotoResponse(photo, UUID.fromString(request.getUserId())));
    responseObserver.onCompleted();
  }

  @Override
  public void listPhoto(FeedRequest request, StreamObserver<PhotoPageResponse> responseObserver) {
    final Page<PhotoJson> photos = photoService.findAllWithFriends(
        request,
        PageRequest.of(request.getPage(), request.getSize())
    );
    responseObserver.onNext(setPhotoPageResponse(photos, UUID.fromString(request.getUserId())));
    responseObserver.onCompleted();
  }

  @Override
  public void stat(StatRequest request, StreamObserver<StatResponse> responseObserver) {
    final List<StatisticJson> stat = statService.stat(request);
    responseObserver.onNext(setStat(stat));
    responseObserver.onCompleted();
  }

  @Override
  public void updatePhoto(PhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
    final PhotoJson photo = photoService.edit(request);
    responseObserver.onNext(setPhotoResponse(photo, UUID.fromString(request.getUserId())));
    responseObserver.onCompleted();
  }

  @Override
  public void photoLike(LikeRequest request, StreamObserver<PhotoResponse> responseObserver) {
    final PhotoJson photo = photoService.updateLike(request);
    responseObserver.onNext(setPhotoResponse(photo, UUID.fromString(request.getRequesterId())));
    responseObserver.onCompleted();
  }

  @Override
  public void deletePhoto(PhotoDeleteRequest request, StreamObserver<PhotoDeleteResponse> responseObserver) {
    final boolean isDeleted = photoService.delete(request);
    responseObserver.onNext(
        PhotoDeleteResponse.newBuilder()
            .setIsDeleted(isDeleted)
            .build()
    );
    responseObserver.onCompleted();
  }

  private @Nonnull PhotoPageResponse setPhotoPageResponse(final Page<PhotoJson> photos, final UUID ownerId) {
    final List<PhotoResponse> photoResponses = photos.stream()
        .map(p -> setPhotoResponse(p, ownerId))
        .sorted(Comparator.comparing(p -> !p.getIsOwner()))
        .toList();
    return PhotoPageResponse.newBuilder()
        .setTotalElements(photos.getTotalElements())
        .setTotalPages(photos.getTotalPages())
        .setFirst(photos.isFirst())
        .setLast(photos.isLast())
        .setSize(photos.getSize())
        .addAllEdges(photoResponses)
        .build();
  }

  private @Nonnull PhotoResponse setPhotoResponse(final PhotoJson photo, final UUID owner) {
    final List<Like> likes = photo.likes()
        .stream()
        .map(l -> Like.newBuilder()
            .setUserId(l.userId().toString())
            .build()
        )
        .toList();
    return PhotoResponse.newBuilder()
        .setId(photo.id().toString())
        .setCountry(guru.qa.rangiffler.grpc.CountryValues.valueOf(photo.country().name()))
        .setDescription(photo.description())
        .setSrc(photo.photo())
        .setUserId(photo.userId().toString())
        .setCreationDate(Timestamps.fromDate(photo.createdDate()))
        .setIsOwner(owner.equals(photo.userId()))
        .addAllLike(likes)
        .build();
  }

  private @Nonnull StatResponse setStat(List<StatisticJson> statJson) {
    final List<CountryStat> stat = statJson.stream()
        .map(s -> CountryStat.newBuilder()
            .setCount(s.count())
            .setCountry(guru.qa.rangiffler.grpc.CountryValues.valueOf(s.country().name()))
            .build())
        .toList();
    return StatResponse.newBuilder()
        .addAllStat(stat)
        .build();
  }
}
