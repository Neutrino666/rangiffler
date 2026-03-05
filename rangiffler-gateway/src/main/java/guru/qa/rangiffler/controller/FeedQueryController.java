package guru.qa.rangiffler.controller;

import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import rangiffler.graphqlTypes.Country;
import rangiffler.graphqlTypes.Feed;
import rangiffler.graphqlTypes.Like;
import rangiffler.graphqlTypes.Likes;
import rangiffler.graphqlTypes.PageInfo;
import rangiffler.graphqlTypes.Photo;
import rangiffler.graphqlTypes.PhotoConnection;
import rangiffler.graphqlTypes.PhotoEdge;
import rangiffler.graphqlTypes.Stat;

@Controller
@PreAuthorize("isAuthenticated()")
public class FeedQueryController {

  @QueryMapping
  public Feed feed(
      @Argument Boolean withFriends
  ) {
    return Feed.newBuilder()
        .photos(
            PhotoConnection.newBuilder()
                .edges(
                    List.of(
                        PhotoEdge.newBuilder()
                            .node(
                                Photo.newBuilder()
                                    .id("21c6b81f-2a87-4a82-8e5b-cac0bc05535b")
                                    .src("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACgAAAAeCAMAAABpA6zvAAABj1BMVEVHcEynHxIAShsAAAAANhcCAgKQExN7JROyIhMBaDAAAAAAPhoCAgKqIRQASx0AAAABAQEAAAAARxoAViN9Ox55EAipHhCoHRC1KRioHRC2KBicFAqeEwqaEwmaEwpxIhACAgIBaTEAAAAEBAQMDAwBaDABazQCAgIAAAAAAAAAYysBZS4CZC4ARxoAAAAAZikAbCwAaSsAcDAAYycAczEAXyUAdjPMGg0AfTndVUkAeDQAUh/haV7XMiPXJxYAWSPXLBraSDsATBwAejbUOy/ZQjTdWE3bTkLib2TTIRLjd27lfXQARhngXVDgY1foj4dorYTuqaLfaF7ZOys2NzbqmpKFvZvUJxhvcG+Li4qiFAqYEwmRQiOzZljfYFW2JhacIhQ0jVnCFwsgICDjc2nniH+6VUfDJRW3Fguqe2QliE9WVlWpPzHxurX65+TNKRedVkODKhNPnnC/i3lVonXNh39GnGuampkUfD+QxKZcXFvNk4h4tpKAgH/219OmKR2RSSllJhBQUVA8k2BNTUx/JbMRAAAAYHRSTlMAQrz+CbwEH//J2DCuNqfLVceCVfX1Y69k1YXC+4VkmnzW6Yos6dmb8mjyq2nZ//////////////////////////////////////////////////////////////////4G/4BLAAAChUlEQVQ4y23U6VPaQBjA4YhQijrejrVja++73JYuJCHB3DEnEKACyqkg4DXeV68/vEkMSkJ/s7Mfdp55P72zEKTnnX7xfsLj8UysPH0y4n0EDeWe80HQm9djo36rg02jpcVB7x7/8vlTJAJ57pXeFobt7W1vbz42mn/n8/levg0FAoFgKAL5/Xaoh2JoTufz0bBZNB6PB1cdEEmTggDDJIKiaPG72dpaPP4t5IQkTKeRMiUIOGJB0zlhpgn0USQnYgguIkWTmS4aSg7CzOkxgmJpFKcRlEXon3fjDBcN2GC+0ERRANIAz6Uxlmyd3Ds7zORPdSgydUACgLPs0Xml78I2eFHLd9KA4WCeF2CBZUua3HfhyCDM52u3FKxwvAAoXkRyTU1uW84BC7u3N1mFb4gcA6sU1pHbh9E7F4wkbPC62esqMJ5jG5IoScUz+cpk4WBwdRBmdi9KPVIplxk2rZS7N0VZk81xQQf8/TfT6eYUDtCNnKLCvdbVuWy5kH1ioXCMsRJTZ1TQ5dheqfKnbTk7rF0XjtVcloRFhlfomFo6OTwM3rlAchD6dwuX+j7QlEpzPBEjjzStbblAMmVbiostvA4TLI2TgMbVS00+M5ixuXZYy5SyksTgCIFQ+n2kVfou4phY63DZLM9ROKXyWbVYCfedE/r38XqDEMsqV5eyfKsS7rshWKIAYFSSVgmpQe9HTWa41cS6HW7EYgRJEQQDAEG1Htz/oBlC6O3fs2G4sLNTreqwapyDB5dM9eHoq7GpmamxheUfZoavHhhMd8lEan3dpcMPEx/H3db/8cg78vzZ4tLyht6vhFFKV19nxyFoZto9/CmZfmXS5XJNTs7OeY2nf9aSCmceryMWAAAAAElFTkSuQmCC")
                                    .country(
                                        Country.newBuilder()
                                            .code("us")
                                            .name("United States")
                                            .flag("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACgAAAAeCAMAAABpA6zvAAABjFBMVEVHcEyDGyNwFCOKGSdIR3CGHSmMIC2OHCt3ERyLGymFFyV1EBqEFiOWIzSSHy9fMEqNHi1JRm9JR3F3EBxGRW9RUXpPT3JQTndyDxmWIzOTHy////+vHzCrHS6zIjVOTH21JjhJSHqSGilgX4r+/v6mGytRUIBTUoHMd4FdXInu0NR8Eh2jGSifITGwITO3KTxFRHXWj5eLi6nKcHvpwsd7epy5N0i+TVulpL1KSXPjs7lycZaCFSFWVYSzP0347O7epKvWjJXfr7XRfYjz3+H68vPJaHTw1tlrapJZWISqKTn9+vrbnqXhqbCfFyXIYm+MGSfq6e1yDxnnvsNBP2++RlX25eeYIzTSgovXkpvDVGKEg6SeKDl3dpvj4eGwMUFFQ2zAYGrlt73Gvb7sy8/Hx8xPRXRlZI3Zlp7S0daMFSL89vfDWmdQT3iQkKy4SVWWlrKAf5/bpqzUh5CpTVe5mJzFa3WfW2Ofnri4hYrfx8nb2Nyra3OpqMCGKDKnRFCzrsGif4K6qaqUQUtekhT/AAAAG3RSTlMACQTuuCBCybZiMJTX161RiXCGfpssHdrZ6+kD0AUcAAADZElEQVQ4y2WUZ3vaSBSFx3EcjFvqbrILihACJLABCSGJJgGid0wz1VRTbGzce+r+8R0BiUPyfpFGus+cc88UACB/v3z97rnMuzevX6w8A3+wpFQA8Neb904n6zw+odx4y92q1+ubW6+UT/VLa8qt5WwWPI852QbB4kajGxdiDXQsxuv1PZl/NhUKxea/Woj55goQNljziai5DacofovFVCqvSaIZvhk3zxGZlHf/ADiPPxkF4wkhONBaFLMJXtWcUNDU6+RNwSN5cH8DnLhgYwk3bhQaRiLm7lu5KllxmUKqnxz16GI6C46hdMyBsm7UhhlPsb51hobjM+FAOcC0RY1ara4/AifqiBHCiYFwYGeydCKYTwbGnAbyEWKxWNQc04EeW7jB1jBEcaOBFYhY1DCT04Ui9lSSpulhqufVqVT7d+C4hgtwNncUrWHCCdUvZso+e0mn+oUjO1n3gJbTIRhgNKiDYqOUra+ZYi1mSL9Pxs9MLAii/Q9Ko7UGOsCNKGsgYgO0l/KHi9aZOwtsQ40giFjuwhzPcLmR6ABlKdRWQKdiiaDd5yfDmeo4Q9KuEvT4HRSoWwLFTjH0FkoXbH0YYiS0YBFmL116QAHHam5qQFFYAaMKg4KcyUduTFYke69UKpnsvjKP6LUe4GQx4sxx8LArMzr4ehEuaubuZHsIooeId13QOhv9JqTSwczLGZ6zwHaRJs/4grr9LjjYVekiqXK4yvN8NRwYSk/LrJsiv3nvPoORK8BZf1kwKAq3xdBl8iZmAZTyNL9zuA2+xK1WLnPhc9ntcNEumGpTjczMWeLcRIwjOxD95RXYEyuRRZMhk+QP881pF3r9jl68PvdCj1e70Ewpn6L9fn8llX/K8CgYyefhxp066N6A0X6H4WbbaZYIbHO4kHnCVJkc5sBjHLahgXsgEAiE28X4j+yak/E1c0Fet0V9Op3O3gCtuTo0JRYdXk/isjsZWJSOt6WHLni8n/6O9FyuTi/y82h5e65kJUCSdLIThMOHb2C0W0qGm2btHLPYLp/bvQsxJO7JzzlwdWnWZrPZ5Q142jeWPVMOD/e+tOFkkiSd08yldjuXWwXa7PKWcm1pfn88W3mhfLW64dme4pGfuVzuw/oaAArl0p+Xklz/dn1VZv3tyxX50//0Z/FTggRVDgAAAABJRU5ErkJggg==")
                                            .build()
                                    )
                                    .description("Нью-Йорк!")
                                    .isOwner(true)
                                    .likes(
                                        Likes.newBuilder()
                                            .total(1)
                                            .likes(List.of(
                                                Like.newBuilder()
                                                    .user("fbe8f306-4bfe-4857-b1f1-090ecf7bd589")
                                                    .build()
                                            ))
                                            .build()
                                    )
                                    .build()
                            )
                            .build())
                )
                .pageInfo(
                    PageInfo.newBuilder()
                        .hasNextPage(false)
                        .hasNextPage(false)
                    .build()
                )
                .build()
        )
        .stat(List.of(
            Stat.newBuilder()
                .count(1)
                .country(Country.newBuilder()
                    .code("ui")
                    .build())
                .build()
        ))
        .build();
  }
}
