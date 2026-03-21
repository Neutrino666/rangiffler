package guru.qa.rangiffler.service;

import guru.qa.rangiffler.data.UserEntity;
import guru.qa.rangiffler.data.repository.UserRepository;
import guru.qa.rangiffler.model.UserJson;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ParametersAreNonnullByDefault
public class UserService {

  private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;
  public final GrpcGeoClient grpcGeoClient;

  @Autowired
  public UserService(UserRepository userRepository, GrpcGeoClient grpcGeoClient) {
    this.userRepository = userRepository;
    this.grpcGeoClient = grpcGeoClient;
  }

  @Transactional
  @KafkaListener(topics = "users", groupId = "userdata")
  public void listener(@Payload UserJson user, ConsumerRecord<String, UserJson> cr) {
    userRepository.findByUsername(user.username())
        .ifPresentOrElse(
            u -> LOG.info("### User already exist in DB, kafka event will be skipped: {}", cr.toString()),
            () -> {
              LOG.info("### Kafka consumer record: {}", cr.toString());

              UserEntity userDataEntity = new UserEntity();
              String countryId = grpcGeoClient.getCountryByCode("ru").getId();
              userDataEntity.setUsername(user.username());
              userDataEntity.setCountryId(UUID.fromString(countryId));
              UserEntity userEntity = userRepository.save(userDataEntity);

              LOG.info(
                  "### User '{}' successfully saved to database with id: {}",
                  user.username(),
                  userEntity.getId()
              );
            }
        );
  }
}
