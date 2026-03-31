package guru.qa.rangiffler.service;

import com.google.protobuf.util.Timestamps;
import guru.qa.rangiffler.grpc.FeedRequest;
import guru.qa.rangiffler.grpc.PhotoDeleteRequest;
import guru.qa.rangiffler.grpc.PhotoDeleteResponse;
import guru.qa.rangiffler.grpc.PhotoPageResponse;
import guru.qa.rangiffler.grpc.PhotoRequest;
import guru.qa.rangiffler.grpc.PhotoResponse;
import guru.qa.rangiffler.grpc.RangifflerPhotoServiceGrpc;
import guru.qa.rangiffler.model.PhotoJson;
import io.grpc.stub.StreamObserver;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@GrpcService
public class GrpcPhotoService extends RangifflerPhotoServiceGrpc.RangifflerPhotoServiceImplBase {

  private final PhotoService photoService;

  @Autowired
  public GrpcPhotoService(PhotoService photoService) {
    this.photoService = photoService;
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
  public void updatePhoto(PhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
    final PhotoJson photo = photoService.edit(request);
    responseObserver.onNext(setPhotoResponse(photo, UUID.fromString(request.getUserId())));
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

  private PhotoPageResponse setPhotoPageResponse(final Page<PhotoJson> photos, final UUID ownerId) {
    List<PhotoResponse> photoResponses = photos.stream()
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

  private PhotoResponse setPhotoResponse(final PhotoJson photo, final UUID owner) {
    return PhotoResponse.newBuilder()
        .setId(photo.id().toString())
        .setCountry(photo.country())
        .setDescription(photo.description())
        .setSrc(photo.photo())
        .setUserId(photo.userId().toString())
        .setCreationDate(Timestamps.fromDate(photo.createdDate()))
        .setIsOwner(owner.equals(photo.userId()))
        .build();
  }
}
