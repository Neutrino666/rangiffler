package guru.qa.rangiffler.controller;

import guru.qa.rangiffler.grpc.CountryPageResponse;
import guru.qa.rangiffler.model.CountryGql;
import guru.qa.rangiffler.service.api.GrpcGeoClient;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import rangiffler.graphqlTypes.Country;

@Controller
@PreAuthorize("isAuthenticated()")
@NoArgsConstructor(access = AccessLevel.NONE)
public class CountriesQueryController {

  private final GrpcGeoClient grpcGeoClient;

  @Autowired
  public CountriesQueryController(final GrpcGeoClient grpcGeoClient) {
    this.grpcGeoClient = grpcGeoClient;
  }

  @QueryMapping
  public List<Country> countries() {
    final CountryPageResponse countries = grpcGeoClient.getCountries();
    return countries.getAllCountriesList()
        .stream()
        .map(CountryGql::fromGrpcCountry)
        .toList();
  }
}
