package guru.qa.rangiffler.controller;

import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.service.api.GrpcGeoClient;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import rangiffler.graphqlTypes.Country;

@Controller
@PreAuthorize("isAuthenticated()")
public class CountriesQueryController {

  private final GrpcGeoClient grpcGeoClient;

  @Autowired
  public CountriesQueryController(GrpcGeoClient grpcGeoClient) {
    this.grpcGeoClient = grpcGeoClient;
  }

  @QueryMapping
  public List<Country> countries() {
    CountryResponse countries = grpcGeoClient.getCountries();
    return countries.getAllCountriesList()
        .stream()
        .map(c ->
            Country.newBuilder()
                .code(c.getCode())
                .name(c.getName())
                .flag(c.getFlag())
                .build())
        .toList();
  }
}
