package guru.qa.rangiffler.model;

import graphql.relay.DefaultConnection;
import guru.qa.rangiffler.grpc.CountryPageResponse;
import guru.qa.rangiffler.grpc.PhotoPageResponse;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rangiffler.graphqlTypes.Photo;

@NoArgsConstructor(access = AccessLevel.NONE)
@ParametersAreNonnullByDefault
public final class PhotoGqlPage {

  public static DefaultConnection<Photo> fromGrpcPhotoPage(
      final PhotoPageResponse photos,
      final CountryPageResponse pageCountries) {
    final List<Photo> pagePhotos = photos.getEdgesList()
        .stream()
        .map(p -> PhotoGql.fromGrpsPhoto(p, pageCountries))
        .toList();
    return new PageGql<>(pagePhotos, photos.getFirst(), photos.getLast()).
        connection();
  }
}
