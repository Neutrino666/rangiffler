package guru.qa.rangiffler.controller;

import guru.qa.rangiffler.grpc.PhotoResponse;
import guru.qa.rangiffler.model.PhotoGql;
import guru.qa.rangiffler.service.api.GrpcGeoClient;
import guru.qa.rangiffler.service.api.GrpcPhotoClient;
import guru.qa.rangiffler.service.api.GrpcUserdataClient;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import rangiffler.graphqlTypes.Photo;
import rangiffler.graphqlTypes.PhotoInput;

@Controller
@PreAuthorize("isAuthenticated()")
public class PhotoMutationController {

  private final GrpcUserdataClient grpcUserdataClient;
  private final GrpcPhotoClient grpcPhotoClient;
  private final GrpcGeoClient grpcGeoClient;

  @Autowired
  public PhotoMutationController(GrpcUserdataClient grpcUserdataClient, GrpcPhotoClient grpcPhotoClient,
      GrpcGeoClient grpcGeoClient) {
    this.grpcUserdataClient = grpcUserdataClient;
    this.grpcPhotoClient = grpcPhotoClient;
    this.grpcGeoClient = grpcGeoClient;
  }

  @MutationMapping
  public Photo photo(
      final @Valid @Argument PhotoInput input,
      final @AuthenticationPrincipal Jwt principal) {
    final String principalUsername = principal.getClaim("sub");
    final UUID userId = grpcUserdataClient.getCurrentUserId(principalUsername);
    final PhotoResponse photoResponse = input.getId() == null
        ? grpcPhotoClient.addPhoto(input, userId)
        : grpcPhotoClient.updatePhoto(input, userId);
    return PhotoGql.fromGrpsPhoto(photoResponse, grpcGeoClient.getCountries());
  }

  @MutationMapping
  public Boolean deletePhoto(
      final @AuthenticationPrincipal Jwt principal,
      final @Valid @Argument String id) {
    final String principalUsername = principal.getClaim("sub");
    final UUID userId = grpcUserdataClient.getCurrentUserId(principalUsername);
    return grpcPhotoClient.deletePhoto(UUID.fromString(id), userId);
  }
}
