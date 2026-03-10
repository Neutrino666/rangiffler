package guru.qa.rangiffler.controller;

import guru.qa.rangiffler.grpc.PhotoResponse;
import guru.qa.rangiffler.service.api.GrpcPhotoClient;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import rangiffler.graphqlTypes.Country;
import rangiffler.graphqlTypes.Likes;
import rangiffler.graphqlTypes.Photo;
import rangiffler.graphqlTypes.PhotoInput;

@Controller
@PreAuthorize("isAuthenticated()")
public class PhotoMutationController {

  private final GrpcPhotoClient grpcGeoClient;

  @Autowired
  public PhotoMutationController(GrpcPhotoClient grpcGeoClient) {
    this.grpcGeoClient = grpcGeoClient;
  }

  @MutationMapping
  public Photo photo(@Valid @Argument PhotoInput input) {
    PhotoResponse res = grpcGeoClient.updatePhoto(input);
    return Photo.newBuilder()
        .id(res.getId())
        .description(res.getDescription())
        .src(res.getSrc())
        .country(Country.newBuilder()
            .code(res.getCountry())
            .name("Afghanistan")
            .flag(
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACgAAAAeCAMAAABpA6zvAAABj1BMVEVHcEynHxIAShsAAAAANhcCAgKQExN7JROyIhMBaDAAAAAAPhoCAgKqIRQASx0AAAABAQEAAAAARxoAViN9Ox55EAipHhCoHRC1KRioHRC2KBicFAqeEwqaEwmaEwpxIhACAgIBaTEAAAAEBAQMDAwBaDABazQCAgIAAAAAAAAAYysBZS4CZC4ARxoAAAAAZikAbCwAaSsAcDAAYycAczEAXyUAdjPMGg0AfTndVUkAeDQAUh/haV7XMiPXJxYAWSPXLBraSDsATBwAejbUOy/ZQjTdWE3bTkLib2TTIRLjd27lfXQARhngXVDgY1foj4dorYTuqaLfaF7ZOys2NzbqmpKFvZvUJxhvcG+Li4qiFAqYEwmRQiOzZljfYFW2JhacIhQ0jVnCFwsgICDjc2nniH+6VUfDJRW3Fguqe2QliE9WVlWpPzHxurX65+TNKRedVkODKhNPnnC/i3lVonXNh39GnGuampkUfD+QxKZcXFvNk4h4tpKAgH/219OmKR2RSSllJhBQUVA8k2BNTUx/JbMRAAAAYHRSTlMAQrz+CbwEH//J2DCuNqfLVceCVfX1Y69k1YXC+4VkmnzW6Yos6dmb8mjyq2nZ//////////////////////////////////////////////////////////////////4G/4BLAAAChUlEQVQ4y23U6VPaQBjA4YhQijrejrVja++73JYuJCHB3DEnEKACyqkg4DXeV68/vEkMSkJ/s7Mfdp55P72zEKTnnX7xfsLj8UysPH0y4n0EDeWe80HQm9djo36rg02jpcVB7x7/8vlTJAJ57pXeFobt7W1vbz42mn/n8/levg0FAoFgKAL5/Xaoh2JoTufz0bBZNB6PB1cdEEmTggDDJIKiaPG72dpaPP4t5IQkTKeRMiUIOGJB0zlhpgn0USQnYgguIkWTmS4aSg7CzOkxgmJpFKcRlEXon3fjDBcN2GC+0ERRANIAz6Uxlmyd3Ds7zORPdSgydUACgLPs0Xml78I2eFHLd9KA4WCeF2CBZUua3HfhyCDM52u3FKxwvAAoXkRyTU1uW84BC7u3N1mFb4gcA6sU1pHbh9E7F4wkbPC62esqMJ5jG5IoScUz+cpk4WBwdRBmdi9KPVIplxk2rZS7N0VZk81xQQf8/TfT6eYUDtCNnKLCvdbVuWy5kH1ioXCMsRJTZ1TQ5dheqfKnbTk7rF0XjtVcloRFhlfomFo6OTwM3rlAchD6dwuX+j7QlEpzPBEjjzStbblAMmVbiostvA4TLI2TgMbVS00+M5ixuXZYy5SyksTgCIFQ+n2kVfou4phY63DZLM9ROKXyWbVYCfedE/r38XqDEMsqV5eyfKsS7rshWKIAYFSSVgmpQe9HTWa41cS6HW7EYgRJEQQDAEG1Htz/oBlC6O3fs2G4sLNTreqwapyDB5dM9eHoq7GpmamxheUfZoavHhhMd8lEan3dpcMPEx/H3db/8cg78vzZ4tLyht6vhFFKV19nxyFoZto9/CmZfmXS5XJNTs7OeY2nf9aSCmceryMWAAAAAElFTkSuQmCC")
            .build())
        .likes(Likes.newBuilder()
            .total(100500)
            .build())
        .build();
  }

  @MutationMapping
  public Boolean deletePhoto(
      @Valid @Argument String id) {
    return true;
  }
}
