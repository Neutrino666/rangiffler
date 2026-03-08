package guru.qa.rangiffler.controller;

import guru.qa.rangiffler.grpc.UserResponse;
import guru.qa.rangiffler.service.api.GrpcUserdataClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import rangiffler.graphqlTypes.Country;
import rangiffler.graphqlTypes.User;


@Controller
@PreAuthorize("isAuthenticated()")
public class UserQueryController {

  private final GrpcUserdataClient grpcUserdataClient;

  @Autowired
  public UserQueryController(GrpcUserdataClient grpcUserdataClient) {
    this.grpcUserdataClient = grpcUserdataClient;
  }

  @QueryMapping
  public User user() {
    UserResponse u = grpcUserdataClient.getCurrentUser("xc");
    return User.newBuilder()
        .id(u.getId())
        .username(u.getUsername())
        .firstname(u.getFirstname())
        .surname(u.getSurname())
        .location(
            Country.newBuilder()
                .code("af")
                .name("Afghanistan")
                .flag("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACgAAAAeCAMAAABpA6zvAAABjFBMVEVHcEyDGyNwFCOKGSdIR3CGHSmMIC2OHCt3ERyLGymFFyV1EBqEFiOWIzSSHy9fMEqNHi1JRm9JR3F3EBxGRW9RUXpPT3JQTndyDxmWIzOTHy////+vHzCrHS6zIjVOTH21JjhJSHqSGilgX4r+/v6mGytRUIBTUoHMd4FdXInu0NR8Eh2jGSifITGwITO3KTxFRHXWj5eLi6nKcHvpwsd7epy5N0i+TVulpL1KSXPjs7lycZaCFSFWVYSzP0347O7epKvWjJXfr7XRfYjz3+H68vPJaHTw1tlrapJZWISqKTn9+vrbnqXhqbCfFyXIYm+MGSfq6e1yDxnnvsNBP2++RlX25eeYIzTSgovXkpvDVGKEg6SeKDl3dpvj4eGwMUFFQ2zAYGrlt73Gvb7sy8/Hx8xPRXRlZI3Zlp7S0daMFSL89vfDWmdQT3iQkKy4SVWWlrKAf5/bpqzUh5CpTVe5mJzFa3WfW2Ofnri4hYrfx8nb2Nyra3OpqMCGKDKnRFCzrsGif4K6qaqUQUtekhT/AAAAG3RSTlMACQTuuCBCybZiMJTX161RiXCGfpssHdrZ6+kD0AUcAAADZElEQVQ4y2WUZ3vaSBSFx3EcjFvqbrILihACJLABCSGJJgGid0wz1VRTbGzce+r+8R0BiUPyfpFGus+cc88UACB/v3z97rnMuzevX6w8A3+wpFQA8Neb904n6zw+odx4y92q1+ubW6+UT/VLa8qt5WwWPI852QbB4kajGxdiDXQsxuv1PZl/NhUKxea/Woj55goQNljziai5DacofovFVCqvSaIZvhk3zxGZlHf/ADiPPxkF4wkhONBaFLMJXtWcUNDU6+RNwSN5cH8DnLhgYwk3bhQaRiLm7lu5KllxmUKqnxz16GI6C46hdMyBsm7UhhlPsb51hobjM+FAOcC0RY1ara4/AifqiBHCiYFwYGeydCKYTwbGnAbyEWKxWNQc04EeW7jB1jBEcaOBFYhY1DCT04Ui9lSSpulhqufVqVT7d+C4hgtwNncUrWHCCdUvZso+e0mn+oUjO1n3gJbTIRhgNKiDYqOUra+ZYi1mSL9Pxs9MLAii/Q9Ko7UGOsCNKGsgYgO0l/KHi9aZOwtsQ40giFjuwhzPcLmR6ABlKdRWQKdiiaDd5yfDmeo4Q9KuEvT4HRSoWwLFTjH0FkoXbH0YYiS0YBFmL116QAHHam5qQFFYAaMKg4KcyUduTFYke69UKpnsvjKP6LUe4GQx4sxx8LArMzr4ehEuaubuZHsIooeId13QOhv9JqTSwczLGZ6zwHaRJs/4grr9LjjYVekiqXK4yvN8NRwYSk/LrJsiv3nvPoORK8BZf1kwKAq3xdBl8iZmAZTyNL9zuA2+xK1WLnPhc9ntcNEumGpTjczMWeLcRIwjOxD95RXYEyuRRZMhk+QP881pF3r9jl68PvdCj1e70Ewpn6L9fn8llX/K8CgYyefhxp066N6A0X6H4WbbaZYIbHO4kHnCVJkc5sBjHLahgXsgEAiE28X4j+yak/E1c0Fet0V9Op3O3gCtuTo0JRYdXk/isjsZWJSOt6WHLni8n/6O9FyuTi/y82h5e65kJUCSdLIThMOHb2C0W0qGm2btHLPYLp/bvQsxJO7JzzlwdWnWZrPZ5Q142jeWPVMOD/e+tOFkkiSd08yldjuXWwXa7PKWcm1pfn88W3mhfLW64dme4pGfuVzuw/oaAArl0p+Xklz/dn1VZv3tyxX50//0Z/FTggRVDgAAAABJRU5ErkJggg==")
                .build()
        )
        .build();
  }
}
