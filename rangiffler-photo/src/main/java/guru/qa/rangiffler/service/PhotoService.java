package guru.qa.rangiffler.service;

import guru.qa.rangiffler.data.CountryValues;
import guru.qa.rangiffler.data.PhotoEntity;
import guru.qa.rangiffler.data.repository.PhotoRepository;
import guru.qa.rangiffler.ex.PhotoNotFoundException;
import guru.qa.rangiffler.grpc.FeedRequest;
import guru.qa.rangiffler.grpc.LikeRequest;
import guru.qa.rangiffler.grpc.PhotoDeleteRequest;
import guru.qa.rangiffler.grpc.PhotoRequest;
import guru.qa.rangiffler.model.PhotoJson;
import guru.qa.rangiffler.util.StringAsByte;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ParametersAreNonnullByDefault
@NoArgsConstructor(access = AccessLevel.NONE)
public class PhotoService {

  private final PhotoRepository photoRepository;
  private final GrpcUserdataClient grpcUserdataClient;

  @Autowired
  public PhotoService(PhotoRepository photoRepository, GrpcUserdataClient grpcUserdataClient) {
    this.photoRepository = photoRepository;
    this.grpcUserdataClient = grpcUserdataClient;
  }

  @Transactional
  public @Nonnull PhotoJson save(final PhotoRequest photo) {
    final PhotoEntity photoEntity = new PhotoEntity();
    photoEntity.setUserId(UUID.fromString(photo.getUserId()));
    photoEntity.setCountry(CountryValues.valueOf(photo.getCountry().getCode().toUpperCase()));
    photoEntity.setDescription(photo.getDescription());
    photoEntity.setPhoto(new StringAsByte(photo.getSrc()).bytes());
    photoEntity.setCreatedDate(new Date());
    return PhotoJson.fromEntity(
        photoRepository.save(photoEntity)
    );
  }

  @Transactional
  public @Nonnull PhotoJson edit(final PhotoRequest photo) {
    return photoRepository.findByIdAndUserId(
            UUID.fromString(photo.getId()),
            UUID.fromString(photo.getUserId())
        )
        .map(photoEntity -> {
          photoEntity.setDescription(photo.getDescription());
          photoEntity.setPhoto(new StringAsByte(photo.getSrc()).bytes());
          photoEntity.setCountry(CountryValues.valueOf(photo.getCountry().getCode().toUpperCase()));
          return PhotoJson.fromEntity(photoRepository.save(photoEntity));
        })
        .orElseThrow(
            () -> new PhotoNotFoundException("Can`t find photo by given id: " + photo.getId())
        );
  }

  @Transactional
  public PhotoJson updateLike(final LikeRequest like) {
    if (!like.getUserId().equals(like.getRequesterId())) {
      final UUID userId = UUID.fromString(like.getUserId());
      final List<UUID> ownerWithFriends = grpcUserdataClient.photoAccessUsers(userId, like.getUsername());
      if (!ownerWithFriends.contains(userId)) {
        throw new RuntimeException("Photo access denied");
      }
    }
    final PhotoEntity photoEntity = photoRepository.findById(UUID.fromString(like.getPhotoId()))
        .orElseThrow();
    photoEntity.updateLikes(UUID.fromString(like.getRequesterId()));
    return PhotoJson.fromEntity(
        photoRepository.save(photoEntity)
    );
  }

  @Transactional
  public boolean delete(final PhotoDeleteRequest request) {
    try {
      photoRepository.deleteByUserIdAndId(
          UUID.fromString(request.getUserId()),
          UUID.fromString(request.getId())
      );
      return true;
    } catch (EmptyResultDataAccessException e) {
      return false;
    }
  }

  @Transactional(readOnly = true)
  public @Nonnull Page<PhotoJson> findAllWithFriends(FeedRequest request, Pageable pageable) {
    final UUID userId = UUID.fromString(request.getUserId());
    final Page<PhotoEntity> photos = request.getWithFriends()
        ? photoRepository.findAllByUserIdIn(
        grpcUserdataClient.photoAccessUsers(UUID.fromString(request.getUserId()), request.getUsername()),
        pageable)
        : photoRepository.findAllByUserId(userId, pageable);
    return photos.map(PhotoJson::fromEntity);
  }
}
