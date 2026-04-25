package guru.qa.rangiffler.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.protobuf.util.Timestamps;
import guru.qa.rangiffler.grpc.RangifflerPhotoServiceGrpc.RangifflerPhotoServiceBlockingStub;
import guru.qa.rangiffler.service.api.GrpcPhotoClient;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import rangiffler.graphqlTypes.CountryInput;
import rangiffler.graphqlTypes.LikeInput;
import rangiffler.graphqlTypes.PhotoInput;

public class GrpcPhotoClientTest {

  private final String grpcCallErrorMessage = "503 SERVICE_UNAVAILABLE \"The gRPC operation was cancelled\"";
  private final String src = "data:image/png;base64,test";
  private final String description = "test photo";
  private final String countryCode = "ru";
  private final String username = "testUser";
  private final String likerId = UUID.randomUUID().toString();
  private final UUID photoId = UUID.randomUUID();
  private final UUID ownerId = UUID.randomUUID();

  private final RangifflerPhotoServiceBlockingStub stub = mock(RangifflerPhotoServiceBlockingStub.class);
  private final GrpcPhotoClient grpcPhotoClient = new GrpcPhotoClient();
  private PhotoPageResponse photosWithFriends;
  private PhotoPageResponse photosWithoutFriends;
  private FeedRequest feedRequestWithFriends;
  private FeedRequest feedRequestWithoutFriends;
  private PhotoResponse photoResponse;
  private PhotoInput testPhotoInput;

  @BeforeEach
  void before() {
    ReflectionTestUtils.setField(grpcPhotoClient, "stub", stub);

    feedRequestWithFriends = FeedRequest.newBuilder()
        .setWithFriends(true)
        .setUserId(ownerId.toString())
        .setPage(0)
        .setSize(2)
        .setUsername(username)
        .build();
    feedRequestWithoutFriends = FeedRequest.newBuilder()
        .setWithFriends(false)
        .setUserId(ownerId.toString())
        .setPage(0)
        .setSize(1)
        .setUsername(username)
        .build();
    photosWithFriends = testPhotos(ownerId, true);
    photosWithoutFriends = testPhotos(ownerId, false);

    photoResponse = PhotoResponse.newBuilder()
        .setId(photoId.toString())
        .setCountry(CountryValues.RU)
        .setDescription(description)
        .setSrc(src)
        .setUserId(ownerId.toString())
        .setCreationDate(Timestamps.fromDate(new Date()))
        .setIsOwner(true)
        .build();

    testPhotoInput = PhotoInput.newBuilder()
        .id(photoId.toString())
        .src(src)
        .country(new CountryInput(countryCode))
        .description(description)
        .build();

    lenient().when(stub.listPhoto(feedRequestWithFriends)).thenReturn(photosWithFriends);
    lenient().when(stub.listPhoto(feedRequestWithoutFriends)).thenReturn(photosWithoutFriends);
    lenient().when(stub.updatePhoto(any(PhotoRequest.class))).thenReturn(photoResponse);
    lenient().when(stub.photoLike(any(LikeRequest.class))).thenReturn(photoResponse);
  }

  @Test
  void listPhotosShouldReturnPhotoPageResponseWithFriends() {
    final PhotoPageResponse actual = grpcPhotoClient.listPhotos(feedRequestWithFriends);
    verify(stub).listPhoto(feedRequestWithFriends);
    assertThat(actual).isEqualTo(photosWithFriends);
  }

  @Test
  void listPhotosShouldReturnPhotoPageResponseWithoutFriends() {
    final PhotoPageResponse actual = grpcPhotoClient.listPhotos(feedRequestWithoutFriends);
    verify(stub).listPhoto(feedRequestWithoutFriends);
    assertThat(actual).isEqualTo(photosWithoutFriends);
  }

  @Test
  void listPhotosShouldThrowResponseStatusException() {
    when(stub.listPhoto(any(FeedRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));
    assertThatThrownBy(() -> grpcPhotoClient.listPhotos(FeedRequest.newBuilder().build()))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessage(grpcCallErrorMessage)
        .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
        .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
  }

  @Test
  void createPhotoShouldReturnPhotoResponse() {
    final PhotoInput photoInput = PhotoInput.newBuilder()
        .id(photoId.toString())
        .src(src)
        .country(new CountryInput(countryCode))
        .description(description)
        .build();
    final PhotoRequest photoRequest = PhotoRequest.newBuilder()
        .setSrc(photoInput.getSrc())
        .setUserId(ownerId.toString())
        .setCountry(
            CountryRequest.newBuilder()
                .setCode(photoInput.getCountry().getCode())
                .build()
        )
        .setDescription(photoInput.getDescription())
        .build();
    when(stub.createPhoto(photoRequest)).thenReturn(photoResponse);
    when(grpcPhotoClient.addPhoto(photoInput, ownerId)).thenReturn(photoResponse);
    final PhotoResponse actual = grpcPhotoClient.addPhoto(photoInput, ownerId);

    verify(stub).createPhoto(photoRequest);
    assertThat(actual).isEqualTo(photoResponse);
  }

  @Test
  void createPhotoShouldThrowResponseStatusException() {
    when(stub.createPhoto(any(PhotoRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));
    assertThatThrownBy(() -> grpcPhotoClient.addPhoto(testPhotoInput, ownerId))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessage(grpcCallErrorMessage)
        .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
        .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
  }


  @Test
  void updatePhotoShouldReturnPhotoResponse() {
    final PhotoResponse actual = grpcPhotoClient.updatePhoto(testPhotoInput, ownerId);
    final PhotoRequest photoRequest = PhotoRequest.newBuilder()
        .setId(testPhotoInput.getId())
        .setUserId(ownerId.toString())
        .setSrc(testPhotoInput.getSrc())
        .setCountry(CountryRequest.newBuilder()
            .setCode(testPhotoInput.getCountry().getCode())
            .build())
        .setDescription(testPhotoInput.getDescription())
        .build();
    verify(stub).updatePhoto(photoRequest);
    assertThat(actual).isEqualTo(photoResponse);
  }

  @Test
  void updateLikeShouldReturnPhotoResponse() {
    final PhotoInput inputWithLike = PhotoInput.newBuilder()
        .id(photoId.toString())
        .src(src)
        .country(new CountryInput(countryCode))
        .description(description)
        .like(new LikeInput(likerId))
        .build();

    final PhotoResponse actual = grpcPhotoClient.updateLike(inputWithLike, ownerId, username);
    final LikeRequest likeRequest = LikeRequest.newBuilder()
        .setUserId(ownerId.toString())
        .setUsername(username)
        .setPhotoId(inputWithLike.getId())
        .setRequesterId(inputWithLike.getLike().getUser())
        .build();

    verify(stub).photoLike(likeRequest);
    assertThat(actual).isEqualTo(photoResponse);
  }

  @Test
  void updateLikeShouldThrowResponseStatusException() {
    final PhotoInput photoInput = PhotoInput.newBuilder()
        .id(photoId.toString())
        .like(LikeInput.newBuilder()
            .user(ownerId.toString())
            .build())
        .build();
    when(stub.photoLike(any(LikeRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));

    assertThatThrownBy(() -> grpcPhotoClient.updateLike(photoInput, ownerId, "test"))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessage(grpcCallErrorMessage)
        .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
        .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
  }

  @Test
  void deletePhotoShouldReturnFalseWhenPhotoNotDeleted() {
    final PhotoDeleteResponse deleteResponse = PhotoDeleteResponse.newBuilder()
        .setIsDeleted(false)
        .build();
    final PhotoDeleteRequest request = PhotoDeleteRequest.newBuilder()
        .setId(photoId.toString())
        .setUserId(ownerId.toString())
        .build();
    when(stub.deletePhoto(any(PhotoDeleteRequest.class))).thenReturn(deleteResponse);
    final Boolean isDeleted = grpcPhotoClient.deletePhoto(photoId, ownerId);

    verify(stub).deletePhoto(request);
    assertThat(isDeleted).isFalse();
  }

  @Test
  void deletePhotoShouldReturnTrueWhenPhotoDeleted() {
    final PhotoDeleteResponse deleteResponse = PhotoDeleteResponse.newBuilder()
        .setIsDeleted(true)
        .build();
    final PhotoDeleteRequest photoDeleteRequest = PhotoDeleteRequest.newBuilder()
        .setId(photoId.toString())
        .setUserId(ownerId.toString())
        .build();
    when(stub.deletePhoto(any(PhotoDeleteRequest.class))).thenReturn(deleteResponse);
    final Boolean actual = grpcPhotoClient.deletePhoto(photoId, ownerId);

    verify(stub).deletePhoto(photoDeleteRequest);
    assertThat(actual).isTrue();
  }

  @Test
  void deletePhotoShouldThrowResponseStatusException() {
    when(stub.deletePhoto(any(PhotoDeleteRequest.class))).thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));
    assertThatThrownBy(() -> grpcPhotoClient.deletePhoto(photoId, ownerId))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessage(grpcCallErrorMessage)
        .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
        .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
  }

  private PhotoPageResponse testPhotos(UUID ownerId, boolean withFriends) {
    final PhotoResponse photo1 = PhotoResponse.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setCountry(CountryValues.RU)
        .setDescription("desc 1")
        .setSrc("data:image/png;base64,1")
        .setUserId(UUID.randomUUID().toString())
        .setCreationDate(Timestamps.fromDate(new Date()))
        .setIsOwner(false)
        .build();
    final PhotoResponse photo2 = PhotoResponse.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setCountry(CountryValues.LV)
        .setDescription("desc 2")
        .setSrc("data:image/png;base64,2")
        .setUserId(ownerId.toString())
        .setCreationDate(Timestamps.fromDate(new Date()))
        .setIsOwner(true)
        .build();
    return PhotoPageResponse.newBuilder()
        .setTotalElements(2)
        .setTotalPages(1)
        .setFirst(true)
        .setLast(true)
        .setSize(2)
        .addAllEdges(withFriends ? List.of(photo1, photo2) : List.of(photo2))
        .build();
  }
}
