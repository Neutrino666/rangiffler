package guru.qa.rangiffler.config;

import guru.qa.rangiffler.service.utils.GqlVoidScalar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RangifflerGatewayServiceConfig {

  private final String rangifflerGatewayBaseUri;

  @Autowired
  public RangifflerGatewayServiceConfig(
      @Value("${rangiffler-gateway.base-uri}") String rangifflerGatewayBaseUri
  ) {
    ;
    this.rangifflerGatewayBaseUri = rangifflerGatewayBaseUri;
  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }

  @Bean
  public RuntimeWiringConfigurer runtimeWiringConfigurer() {
    return wiringBuilder -> wiringBuilder.scalar(GqlVoidScalar.Void);
  }
}
