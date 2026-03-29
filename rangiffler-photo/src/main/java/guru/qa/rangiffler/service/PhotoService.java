package guru.qa.rangiffler.service;

import guru.qa.rangiffler.data.PhotoEntity;
import guru.qa.rangiffler.data.repository.PhotoRepository;
import guru.qa.rangiffler.ex.PhotoNotFoundException;
import guru.qa.rangiffler.grpc.FeedRequest;
import guru.qa.rangiffler.grpc.PhotoDeleteRequest;
import guru.qa.rangiffler.grpc.PhotoRequest;
import guru.qa.rangiffler.model.PhotoJson;
import guru.qa.rangiffler.util.StringAsByte;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@ParametersAreNonnullByDefault
public class PhotoService {

  private final PhotoRepository photoRepository;

  @Autowired
  public PhotoService(PhotoRepository photoRepository) {
    this.photoRepository = photoRepository;
  }

  @Transactional
  public @Nonnull PhotoJson save(PhotoRequest photo) {
    PhotoEntity photoEntity = new PhotoEntity();
    photoEntity.setUserId(UUID.fromString(photo.getUserId()));
    photoEntity.setCountry(photo.getCountry().getCode());
    photoEntity.setDescription(photo.getDescription());
    photoEntity.setPhoto(new StringAsByte(photo.getSrc()).bytes());
    photoEntity.setCreatedDate(new Date());
    return PhotoJson.fromEntity(
        photoRepository.save(photoEntity)
    );
  }

  @Transactional
  public @Nonnull PhotoJson edit(PhotoRequest photo) {
    return photoRepository.findByIdAndUserId(
            UUID.fromString(photo.getId()),
            UUID.fromString(photo.getUserId())
        )
        .map(photoEntity -> {
          photoEntity.setDescription(photo.getDescription());
          photoEntity.setPhoto(new StringAsByte(photo.getSrc()).bytes());
          photoEntity.setCountry(photo.getCountry().getCode());
          return PhotoJson.fromEntity(photoRepository.save(photoEntity));
        })
        .orElseThrow(
            () -> new PhotoNotFoundException("Can`t find photo by given id: " + photo.getId())
        );
  }

  @Transactional
  public boolean delete(PhotoDeleteRequest request) {
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
  public @Nonnull Page<PhotoJson> findAllByUserId(FeedRequest request, Pageable pageable) {
    return photoRepository.findAllByUserId(UUID.fromString(request.getUserId()), pageable)
        .map(PhotoJson::fromEntity);
  }
}
