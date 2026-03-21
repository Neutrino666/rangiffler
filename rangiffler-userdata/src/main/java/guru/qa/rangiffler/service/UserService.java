package guru.qa.rangiffler.service;

import guru.qa.rangiffler.data.UserEntity;
import guru.qa.rangiffler.data.repository.UserRepository;
import guru.qa.rangiffler.model.UserJson;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ParametersAreNonnullByDefault
public class UserService {

  private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  @KafkaListener(topics = "users", groupId = "userdata")
  public void listener(@Payload UserJson user, ConsumerRecord<String, UserJson> cr) {
    userRepository.findByUsername(user.username())
        .ifPresentOrElse(
            u -> LOG.info("### User already exist in DB, kafka event will be skipped: {}", cr.toString()),
            () -> {
              LOG.info("### Kafka consumer record: {}", cr.toString());

              UserEntity ue = new UserEntity();
              ue.setUsername(user.username());
              ue.setCountry(GrpcUserService.DEFAULT_COUNTRY);
              UserEntity userEntity = userRepository.save(ue);

              LOG.info(
                  "### User '{}' successfully saved to database with id: {}",
                  user.username(),
                  userEntity.getId()
              );
            }
        );
  }

  @Transactional(readOnly = true)
  public @Nonnull
  Page<UserEntity> allUsers(
      String username,
      Pageable pageable,
      @Nullable String searchQuery) {
    return searchQuery == null
        ? userRepository.findByUsernameNot(username, pageable)
        : userRepository.findByUsernameNotAndSearchQuery(username, searchQuery, pageable);
  }
}
