package guru.qa.rangiffler.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import guru.qa.rangiffler.data.CountryValues;
import guru.qa.rangiffler.data.LikeEntity;
import guru.qa.rangiffler.data.PhotoEntity;
import guru.qa.rangiffler.data.repository.PhotoRepository;
import guru.qa.rangiffler.ex.PhotoNotFoundException;
import guru.qa.rangiffler.grpc.CountryRequest;
import guru.qa.rangiffler.grpc.FeedRequest;
import guru.qa.rangiffler.grpc.LikeRequest;
import guru.qa.rangiffler.grpc.PhotoDeleteRequest;
import guru.qa.rangiffler.grpc.PhotoRequest;
import guru.qa.rangiffler.model.LikeJson;
import guru.qa.rangiffler.model.PhotoJson;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ParametersAreNonnullByDefault
@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {

  private final String photoSrc = "data:image/png;base64,R0lGODlhAQA";
  private final String description = "testDescription";
  private final CountryValues country = CountryValues.RU;
  private final UUID photoId = UUID.randomUUID();
  private final UUID userId = UUID.randomUUID();

  @Mock
  private PhotoRepository photoRepository;

  @Mock
  private GrpcUserdataClient grpcUserdataClient;

  @InjectMocks
  private PhotoService photoService;

  private PhotoEntity mainPhoto;

  @Captor
  ArgumentCaptor<PhotoEntity> photoCaptor;

  @BeforeEach
  void before() {
    mainPhoto = new PhotoEntity();
    mainPhoto.setId(UUID.randomUUID());
    mainPhoto.setUserId(UUID.randomUUID());
    mainPhoto.setCountry(country);
    mainPhoto.setDescription(description);
    mainPhoto.setPhoto(photoSrc.getBytes());
    mainPhoto.setCreatedDate(new Date());
  }

  @Test
  void saveShouldCreateAndReturnPhotoJsonWhenValidRequest() {
    final PhotoRequest request = buildPhotoRequest(null, userId);
    final PhotoEntity savedEntity = updateMainPhoto(UUID.randomUUID(), userId);

    when(photoRepository.save(any(PhotoEntity.class))).thenReturn(savedEntity);

    final PhotoJson result = photoService.save(request);

    assertThat(result).isEqualTo(PhotoJson.fromEntity(savedEntity));
    verify(photoRepository).save(photoCaptor.capture());
    final PhotoEntity captured = photoCaptor.getValue();
    assertThat(captured.getId()).isNull();
    assertThat(captured.getUserId()).isEqualTo(userId);
    assertThat(captured.getCountry()).isEqualTo(country);
    assertThat(captured.getDescription()).isEqualTo(description);
    assertThat(captured.getCreatedDate()).isNotNull();
  }

  @Test
  void editShouldUpdateAndReturnPhotoJsonWhenPhotoExists() {
    final PhotoRequest request = buildPhotoRequest(photoId, userId);
    final PhotoEntity existingEntity = updateMainPhoto(photoId, userId);
    final PhotoEntity updatedEntity = updateMainPhoto(photoId, userId);
    updatedEntity.setDescription("Updated description");

    when(photoRepository.findByIdAndUserId(eq(photoId), eq(userId))).thenReturn(Optional.of(existingEntity));
    when(photoRepository.save(eq(updatedEntity))).thenReturn(updatedEntity);

    final PhotoJson result = photoService.edit(request);

    verify(photoRepository).findByIdAndUserId(photoId, userId);
    verify(photoRepository).save(existingEntity);
    assertThat(result).isEqualTo(PhotoJson.fromEntity(updatedEntity));
  }

  @Test
  void editShouldThrowPhotoNotFoundExceptionWhenPhotoNotFound() {
    final PhotoRequest request = buildPhotoRequest(photoId, userId);

    when(photoRepository.findByIdAndUserId(eq(photoId), eq(userId))).thenReturn(Optional.empty());

    assertThatThrownBy(() -> photoService.edit(request))
        .isInstanceOf(PhotoNotFoundException.class)
        .hasMessage("Can`t find photo by given id: " + photoId);
  }

  @Test
  void updateLikeShouldAddLikeWhenRequesterIsOwner() {
    final PhotoEntity photoEntity = updateMainPhoto(photoId, userId);
    final LikeRequest like = LikeRequest.newBuilder()
        .setUserId(userId.toString())
        .setUsername("owner")
        .setPhotoId(photoId.toString())
        .setRequesterId(userId.toString())
        .build();
    final LikeEntity likeEntity = new LikeEntity();
    likeEntity.setUserId(photoEntity.getUserId());

    when(photoRepository.findById(eq(photoId))).thenReturn(Optional.of(photoEntity));
    when(photoRepository.save(eq(photoEntity))).thenReturn(photoEntity);

    final PhotoJson result = photoService.updateLike(like);

    assertThat(result.likes())
        .hasSize(1)
        .contains(LikeJson.fromLikeEntity(likeEntity));
    assertThat(result)
        .hasFieldOrPropertyWithValue("id", photoId)
        .hasFieldOrPropertyWithValue("userId", userId);
    assertThat(photoEntity.getLikes())
        .hasSize(1)
        .extracting(LikeEntity::getUserId)
        .contains(userId);
    verify(photoRepository).findById(photoId);
    verify(photoRepository).save(photoEntity);
  }

  @Test
  void updateLikeShouldAddLikeWhenRequesterIsFriend() {
    final UUID requesterId = UUID.randomUUID();
    final PhotoEntity photoEntity = updateMainPhoto(photoId, userId);
    final String username = "friend";
    final LikeRequest like = LikeRequest.newBuilder()
        .setUserId(userId.toString())
        .setUsername(username)
        .setPhotoId(photoId.toString())
        .setRequesterId(requesterId.toString())
        .build();

    when(grpcUserdataClient.photoAccessUsers(eq(userId), eq(username)))
        .thenReturn(List.of(userId, requesterId));
    when(photoRepository.findById(eq(photoId))).thenReturn(Optional.of(photoEntity));
    when(photoRepository.save(eq(photoEntity))).thenReturn(photoEntity);

    final PhotoJson result = photoService.updateLike(like);

    assertThat(photoEntity.getLikes())
        .extracting(LikeEntity::getUserId)
        .containsExactly(requesterId);
    assertThat(result)
        .hasFieldOrPropertyWithValue("id", photoId)
        .hasFieldOrPropertyWithValue("userId", userId);
    assertThat(result.likes())
        .extracting(LikeJson::userId)
        .containsExactly(requesterId);
    verify(grpcUserdataClient).photoAccessUsers(userId, username);
  }

  @Test
  void updateLikeShouldThrowRuntimeExceptionWhenUserHasNoAccess() {
    final UUID requesterId = UUID.randomUUID();
    final String username = "noAccessUser";
    final LikeRequest like = LikeRequest.newBuilder()
        .setUserId(userId.toString())
        .setUsername(username)
        .setPhotoId(photoId.toString())
        .setRequesterId(requesterId.toString())
        .build();

    when(grpcUserdataClient.photoAccessUsers(eq(userId), eq(username)))
        .thenReturn(List.of(UUID.randomUUID(), UUID.randomUUID()));

    assertThatThrownBy(() -> photoService.updateLike(like))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Photo access denied");

    verify(grpcUserdataClient).photoAccessUsers(userId, username);
  }

  @Test
  void deleteShouldReturnTrueWhenPhotoDeletedSuccessfully() {
    final boolean result = photoService.delete(buildPhotoDeleteRequest());

    assertThat(result).isTrue();
    verify(photoRepository).deleteByUserIdAndId(userId, photoId);
  }

  @Test
  void deleteShouldReturnFalseWhenPhotoNotFound() {
    doThrow(new EmptyResultDataAccessException(1))
        .when(photoRepository).deleteByUserIdAndId(eq(userId), eq(photoId));

    final boolean result = photoService.delete(buildPhotoDeleteRequest());

    assertThat(result).isFalse();
  }

  @Test
  void findAllWithFriendsShouldReturnOwnPhotosWhenWithFriendsIsFalse() {
    final PhotoEntity entity = updateMainPhoto(UUID.randomUUID(), userId);
    final Page<PhotoEntity> photoPage = new PageImpl<>(List.of(entity));
    final FeedRequest request = buildFeedRequest(false);
    final Pageable pageable = PageRequest.of(0, 10);

    when(photoRepository.findAllByUserId(eq(userId), eq(pageable))).thenReturn(photoPage);

    final Page<PhotoJson> result = photoService.findAllWithFriends(request, pageable);

    assertThat(result)
        .extracting(photoJson -> photoJson)
        .contains(PhotoJson.fromEntity(entity));
    verify(photoRepository).findAllByUserId(userId, pageable);
  }

  @Test
  void findAllWithFriendsShouldReturnPhotosOfUserAndFriendsWhenWithFriendsIsTrue() {
    final UUID friendId = UUID.randomUUID();
    final PhotoEntity ownPhoto = updateMainPhoto(UUID.randomUUID(), userId);
    final PhotoEntity friendPhoto = updateMainPhoto(UUID.randomUUID(), friendId);
    final Page<PhotoEntity> photoPage = new PageImpl<>(List.of(ownPhoto, friendPhoto));
    final FeedRequest request = buildFeedRequest(true);
    final String username = request.getUsername();
    final UUID userId = UUID.fromString(request.getUserId());
    final List<UUID> accessUsers = List.of(userId, friendId);
    final Pageable pageable = PageRequest.of(0, 10);

    when(grpcUserdataClient.photoAccessUsers(eq(userId), eq(username))).thenReturn(accessUsers);
    when(photoRepository.findAllByUserIdIn(eq(accessUsers), eq(pageable))).thenReturn(photoPage);

    final Page<PhotoJson> result = photoService.findAllWithFriends(request, pageable);

    assertThat(result)
        .extracting(photoJson -> photoJson)
        .containsExactlyInAnyOrder(PhotoJson.fromEntity(ownPhoto), PhotoJson.fromEntity(friendPhoto));
    verify(grpcUserdataClient).photoAccessUsers(userId, username);
    verify(photoRepository).findAllByUserIdIn(accessUsers, pageable);
  }

  private PhotoEntity updateMainPhoto(UUID id, UUID userId) {
    mainPhoto.setId(id);
    mainPhoto.setUserId(userId);
    return mainPhoto;
  }

  private PhotoRequest buildPhotoRequest(@Nullable UUID id, UUID userId) {
    return PhotoRequest.newBuilder()
        .setId(id != null ? id.toString() : "")
        .setSrc(photoSrc)
        .setCountry(CountryRequest.newBuilder().setCode(country.name().toLowerCase()).build())
        .setDescription(description)
        .setUserId(userId.toString())
        .build();
  }

  private PhotoDeleteRequest buildPhotoDeleteRequest() {
    return PhotoDeleteRequest.newBuilder()
        .setUserId(userId.toString())
        .setId(photoId.toString())
        .build();
  }

  private FeedRequest buildFeedRequest(boolean withFriends) {
    return FeedRequest.newBuilder()
        .setWithFriends(withFriends)
        .setUserId(userId.toString())
        .setUsername("feedRequestUser")
        .setPage(0)
        .setSize(10)
        .build();
  }
}
