package guru.qa.rangiffler.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import guru.qa.rangiffler.data.CountryValues;
import guru.qa.rangiffler.data.projection.UserCountrySum;
import guru.qa.rangiffler.data.repository.PhotoRepository;
import guru.qa.rangiffler.grpc.StatRequest;
import guru.qa.rangiffler.model.StatisticJson;
import java.util.List;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ParametersAreNonnullByDefault
@ExtendWith(MockitoExtension.class)
class StatServiceTest {

  private final UUID userId = UUID.randomUUID();
  private final UUID friendId = UUID.randomUUID();
  private final String username = "username";

  @Mock
  private PhotoRepository photoRepository;

  @Autowired
  @Mock
  private GrpcUserdataClient grpcUserdataClient;

  @InjectMocks
  private StatService statService;

  @Test
  void statShouldReturnStatisticsForCurrentUserOnlyWhenWithoutFriends() {
    final StatRequest request = statRequestByWithFriends(false);
    final UserCountrySum ruSum = new UserCountrySum(5L, CountryValues.RU);
    final UserCountrySum usSum = new UserCountrySum(3L, CountryValues.US);
    final List<UserCountrySum> expected = List.of(ruSum, usSum);

    when(photoRepository.statisticByUser(eq(List.of(userId)))).thenReturn(expected);

    final List<StatisticJson> result = statService.stat(request);

    assertThat(result).isEqualTo(StatisticJson.fromUserCountrySums(expected));
    verify(photoRepository).statisticByUser(List.of(userId));
    verify(grpcUserdataClient, never()).photoAccessUsers(any(), any());
  }

  @Test
  void statShouldReturnStatisticsIncludingFriendsWhenWithFriends() {
    final StatRequest request = statRequestByWithFriends(true);
    final List<UUID> userIds = List.of(userId, friendId);
    final UserCountrySum ruSum = new UserCountrySum(10L, CountryValues.RU);
    final List<UserCountrySum> stat = List.of(ruSum);

    when(grpcUserdataClient.photoAccessUsers(eq(userId), eq(username))).thenReturn(userIds);
    when(photoRepository.statisticByUser(eq(userIds))).thenReturn(stat);

    final List<StatisticJson> result = statService.stat(request);

    assertThat(result).isEqualTo(StatisticJson.fromUserCountrySums(stat));
    verify(grpcUserdataClient).photoAccessUsers(userId, username);
    verify(photoRepository).statisticByUser(userIds);
  }

  @Test
  void statShouldReturnEmptyListWhenNoPhotosExistWhenWithoutFriends() {
    final StatRequest request = statRequestByWithFriends(false);

    when(photoRepository.statisticByUser(eq(List.of(userId)))).thenReturn(List.of());

    final List<StatisticJson> result = statService.stat(request);

    assertThat(result).isEmpty();
    verify(photoRepository).statisticByUser(List.of(userId));
    verify(grpcUserdataClient, never()).photoAccessUsers(any(), any());
  }

  @Test
  void statShouldReturnEmptyListWhenWithFriendsAndNoPhotosExist() {
    final StatRequest request = statRequestByWithFriends(true);
    final List<UUID> userIds = List.of(userId, friendId);

    when(grpcUserdataClient.photoAccessUsers(eq(userId), eq(username))).thenReturn(userIds);
    when(photoRepository.statisticByUser(eq(userIds))).thenReturn(List.of());

    final List<StatisticJson> result = statService.stat(request);

    assertThat(result).isEmpty();
    verify(grpcUserdataClient).photoAccessUsers(userId, username);
    verify(photoRepository).statisticByUser(userIds);
  }

  @Test
  void statShouldReturnMultipleCountriesWithCorrectCountsWhenWithFriends() {
    final StatRequest request = statRequestByWithFriends(true);
    final List<UUID> userIds = List.of(userId, friendId);
    final UserCountrySum ruSum = new UserCountrySum(7L, CountryValues.RU);
    final UserCountrySum deSum = new UserCountrySum(4L, CountryValues.DE);
    final UserCountrySum frSum = new UserCountrySum(2L, CountryValues.FR);
    final List<UserCountrySum> expected = List.of(ruSum, deSum, frSum);

    when(grpcUserdataClient.photoAccessUsers(eq(userId), eq(username))).thenReturn(userIds);
    when(photoRepository.statisticByUser(eq(userIds))).thenReturn(expected);

    final List<StatisticJson> result = statService.stat(request);

    assertThat(result).containsExactlyInAnyOrderElementsOf(StatisticJson.fromUserCountrySums(expected));
    verify(photoRepository).statisticByUser(userIds);
    verify(grpcUserdataClient).photoAccessUsers(userId, username);
  }

  private StatRequest statRequestByWithFriends(boolean withFriends) {
    return StatRequest.newBuilder()
        .setUserId(userId.toString())
        .setUsername(username)
        .setWithFriends(withFriends)
        .build();
  }
}
