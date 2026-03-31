package guru.qa.rangiffler.service;

import guru.qa.rangiffler.grpc.FeedRequest;
import guru.qa.rangiffler.grpc.RangifflerUserdataServiceGrpc;
import guru.qa.rangiffler.grpc.UsersRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
@ParametersAreNonnullByDefault
public class GrpcUserdataClient {

  @GrpcClient("grpcUserdataClient")
  private RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub rangifflerUserdataServiceStub;

  @Nonnull
  public List<UUID> photoUsers(final FeedRequest request) {
    List<UUID> result = new ArrayList<>();
    result.add(UUID.fromString(request.getUserId()));
    final List<UUID> friends = rangifflerUserdataServiceStub.listFriendsIds(
            UsersRequest.newBuilder()
                .setUsername(request.getUsername())
                .build()
        )
        .getIdList()
        .stream()
        .map(UUID::fromString)
        .toList();
    result.addAll(friends);
    return result;
  }
}
