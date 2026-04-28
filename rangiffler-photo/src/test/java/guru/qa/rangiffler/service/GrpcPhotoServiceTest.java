package guru.qa.rangiffler.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.protobuf.util.Timestamps;
import guru.qa.rangiffler.data.CountryValues;
import guru.qa.rangiffler.grpc.CountryRequest;
import guru.qa.rangiffler.grpc.CountryStat;
import guru.qa.rangiffler.grpc.FeedRequest;
import guru.qa.rangiffler.grpc.Like;
import guru.qa.rangiffler.grpc.LikeRequest;
import guru.qa.rangiffler.grpc.PhotoDeleteRequest;
import guru.qa.rangiffler.grpc.PhotoDeleteResponse;
import guru.qa.rangiffler.grpc.PhotoPageResponse;
import guru.qa.rangiffler.grpc.PhotoRequest;
import guru.qa.rangiffler.grpc.PhotoResponse;
import guru.qa.rangiffler.grpc.StatRequest;
import guru.qa.rangiffler.grpc.StatResponse;
import guru.qa.rangiffler.model.LikeJson;
import guru.qa.rangiffler.model.PhotoJson;
import guru.qa.rangiffler.model.StatisticJson;
import io.grpc.stub.StreamObserver;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ParametersAreNonnullByDefault
@ExtendWith(MockitoExtension.class)
public class GrpcPhotoServiceTest {

  @Mock
  private PhotoService photoService;

  @Mock
  private StatService statService;

  @Mock
  private StreamObserver<PhotoResponse> photoResponseStreamObserver;

  @Mock
  private StreamObserver<StatResponse> statResponseStreamObserver;

  @Mock
  private StreamObserver<PhotoPageResponse> photoPageResponseStreamObserver;

  @Mock
  private StreamObserver<PhotoDeleteResponse> photoDeleteResponseStreamObserver;

  @InjectMocks
  private GrpcPhotoService grpcPhotoService;

  @Captor
  private ArgumentCaptor<PhotoResponse> photoResponseCaptor;

  @Captor
  private ArgumentCaptor<StatResponse> statResponseCaptor;

  @Captor
  private ArgumentCaptor<PhotoPageResponse> photoPageResponseCaptor;

  @Captor
  private ArgumentCaptor<PhotoDeleteResponse> photoDeleteResponseCaptor;

  private final UUID ownerId = UUID.randomUUID();

  private final PhotoJson photo = new PhotoJson(
      UUID.randomUUID(),
      ownerId,
      CountryValues.AD,
      "test description",
      "data:image/png;base64,R0lGODlhAQA",
      new Date(),
      List.of(new LikeJson(UUID.randomUUID()))
  );

  private final PhotoRequest photoRequest = PhotoRequest.newBuilder()
      .setUserId(ownerId.toString())
      .setCountry(
          CountryRequest.newBuilder()
              .setCode(CountryValues.AD.getCode())
              .build()
      )
      .setDescription("test description")
      .setSrc("data:image/png;base64,R0lGODlhAQA")
      .build();

  private final FeedRequest feedRequest = FeedRequest.newBuilder()
      .setUserId(ownerId.toString())
      .setPage(0)
      .setSize(10)
      .build();

  private final LikeRequest likeRequest = LikeRequest.newBuilder()
      .setPhotoId(photo.id().toString())
      .setRequesterId(ownerId.toString())
      .setUserId(ownerId.toString())
      .setUsername("test")
      .build();

  private final PhotoDeleteRequest deleteRequest = PhotoDeleteRequest.newBuilder()
      .setUserId(ownerId.toString())
      .setId(photo.id().toString())
      .build();

  private final StatRequest statRequest = StatRequest.newBuilder()
      .setUserId(ownerId.toString())
      .setUsername("test")
      .build();

  @Test
  void createPhotoShouldCallPhotoService() {
    final PhotoResponse expected = photoResponseFromPhotoJson(photo, ownerId);
    when(photoService.save(eq(photoRequest))).thenReturn(photo);
    grpcPhotoService.createPhoto(photoRequest, photoResponseStreamObserver);

    verify(photoService).save(photoRequest);
    verify(photoResponseStreamObserver).onNext(photoResponseCaptor.capture());
    verify(photoResponseStreamObserver).onCompleted();
    final PhotoResponse actual = photoResponseCaptor.getValue();
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void listPhotoShouldCallPhotoService() {
    Page<PhotoJson> photoPage = new PageImpl<>(List.of(photo));
    final PhotoPageResponse expected = photoPageResponseFromPagePhotoJson(photoPage, ownerId);
    PageRequest pageRequest = PageRequest.of(feedRequest.getPage(), feedRequest.getSize());
    when(photoService.findAllWithFriends(eq(feedRequest), eq(pageRequest)))
        .thenReturn(photoPage);
    grpcPhotoService.listPhoto(feedRequest, photoPageResponseStreamObserver);

    verify(photoService).findAllWithFriends(
        eq(feedRequest),
        eq(pageRequest));
    verify(photoPageResponseStreamObserver).onNext(photoPageResponseCaptor.capture());
    verify(photoPageResponseStreamObserver).onCompleted();
    final PhotoPageResponse actual = photoPageResponseCaptor.getValue();
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void statShouldCallStatService() {
    final List<StatisticJson> stats = List.of(
        new StatisticJson(5, CountryValues.AD),
        new StatisticJson(3, CountryValues.RU)
    );
    final StatResponse expected = statResponseFromStatisticJsonList(stats);
    when(statService.stat(eq(statRequest))).thenReturn(stats);
    grpcPhotoService.stat(statRequest, statResponseStreamObserver);

    verify(statService).stat(statRequest);
    verify(statResponseStreamObserver).onNext(statResponseCaptor.capture());
    verify(statResponseStreamObserver).onCompleted();
    final StatResponse actual = statResponseCaptor.getValue();
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void updatePhotoShouldCallPhotoService() {
    final PhotoResponse expected = photoResponseFromPhotoJson(photo, ownerId);
    when(photoService.edit(eq(photoRequest))).thenReturn(photo);
    grpcPhotoService.updatePhoto(photoRequest, photoResponseStreamObserver);

    verify(photoService).edit(photoRequest);
    verify(photoResponseStreamObserver).onNext(photoResponseCaptor.capture());
    verify(photoResponseStreamObserver).onCompleted();
    final PhotoResponse actual = photoResponseCaptor.getValue();
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void photoLikeShouldCallPhotoService() {
    final PhotoResponse expected = photoResponseFromPhotoJson(photo, ownerId);
    when(photoService.updateLike(eq(likeRequest))).thenReturn(photo);
    grpcPhotoService.photoLike(likeRequest, photoResponseStreamObserver);

    verify(photoService).updateLike(likeRequest);
    verify(photoResponseStreamObserver).onNext(photoResponseCaptor.capture());
    verify(photoResponseStreamObserver).onCompleted();
    final PhotoResponse actual = photoResponseCaptor.getValue();
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void deletePhotoShouldCallPhotoServiceWhenDeleted() {
    when(photoService.delete(eq(deleteRequest))).thenReturn(true);
    grpcPhotoService.deletePhoto(deleteRequest, photoDeleteResponseStreamObserver);

    verify(photoService).delete(deleteRequest);
    verify(photoDeleteResponseStreamObserver).onNext(photoDeleteResponseCaptor.capture());
    verify(photoDeleteResponseStreamObserver).onCompleted();
    final PhotoDeleteResponse actual = photoDeleteResponseCaptor.getValue();
    assertThat(actual.getIsDeleted()).isTrue();
  }

  @Test
  void deletePhotoShouldCallPhotoServiceWhenNotDeleted() {
    when(photoService.delete(eq(deleteRequest))).thenReturn(false);
    grpcPhotoService.deletePhoto(deleteRequest, photoDeleteResponseStreamObserver);

    verify(photoService).delete(deleteRequest);
    verify(photoDeleteResponseStreamObserver).onNext(photoDeleteResponseCaptor.capture());
    verify(photoDeleteResponseStreamObserver).onCompleted();
    final PhotoDeleteResponse actual = photoDeleteResponseCaptor.getValue();
    assertThat(actual.getIsDeleted()).isFalse();
  }

  private @Nonnull PhotoResponse photoResponseFromPhotoJson(final PhotoJson photo, final UUID ownerId) {
    final List<Like> likes = photo.likes().stream()
        .map(l -> Like.newBuilder().setUserId(l.userId().toString()).build())
        .toList();
    return PhotoResponse.newBuilder()
        .setId(photo.id().toString())
        .setCountry(guru.qa.rangiffler.grpc.CountryValues.valueOf(photo.country().name()))
        .setDescription(photo.description())
        .setSrc(photo.photo())
        .setUserId(photo.userId().toString())
        .setCreationDate(Timestamps.fromDate(photo.createdDate()))
        .setIsOwner(ownerId.equals(photo.userId()))
        .addAllLike(likes)
        .build();
  }

  private @Nonnull PhotoPageResponse photoPageResponseFromPagePhotoJson(
      final Page<PhotoJson> photos, final UUID ownerId) {
    final List<PhotoResponse> photoResponses = photos.stream()
        .map(p -> photoResponseFromPhotoJson(p, ownerId))
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

  private @Nonnull StatResponse statResponseFromStatisticJsonList(final List<StatisticJson> statJsonList) {
    final List<CountryStat> stat = statJsonList.stream()
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
