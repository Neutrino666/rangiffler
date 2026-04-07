package guru.qa.rangiffler.service;

import guru.qa.rangiffler.data.projection.UserCountrySum;
import guru.qa.rangiffler.data.repository.PhotoRepository;
import guru.qa.rangiffler.grpc.StatRequest;
import guru.qa.rangiffler.model.StatisticJson;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ParametersAreNonnullByDefault
@NoArgsConstructor(access = AccessLevel.NONE)
public class StatService {

  private final PhotoRepository photoRepository;
  private final GrpcUserdataClient grpcUserdataClient;

  @Autowired
  public StatService(PhotoRepository photoRepository, GrpcUserdataClient grpcUserdataClient) {
    this.photoRepository = photoRepository;
    this.grpcUserdataClient = grpcUserdataClient;
  }

  @Transactional
  public @Nonnull List<StatisticJson> stat(final StatRequest stat) {
    final UUID userId = UUID.fromString(stat.getUserId());
    final List<UUID> userIds = stat.getWithFriends()
        ? grpcUserdataClient.photoAccessUsers(userId, stat.getUsername())
        : List.of(userId);
    final List<UserCountrySum> userCountrySums = photoRepository.statisticByUser(userIds);
    return StatisticJson.fromUserCountrySums(userCountrySums);
  }
}
