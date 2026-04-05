package guru.qa.rangiffler.service;

import static guru.qa.rangiffler.model.FriendshipStatus.FRIEND;
import static guru.qa.rangiffler.model.FriendshipStatus.INVITATION_SENT;

import guru.qa.rangiffler.data.CountryValues;
import guru.qa.rangiffler.data.FriendshipEntity;
import guru.qa.rangiffler.data.FriendshipStatus;
import guru.qa.rangiffler.data.UserEntity;
import guru.qa.rangiffler.data.projection.UserWithStatus;
import guru.qa.rangiffler.data.repository.UserRepository;
import guru.qa.rangiffler.ex.NotFoundException;
import guru.qa.rangiffler.ex.SameUsernameException;
import guru.qa.rangiffler.grpc.UserRequest;
import guru.qa.rangiffler.model.UserJson;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@ParametersAreNonnullByDefault
public class UserService {

  public static final CountryValues DEFAULT_COUNTRY = CountryValues.RU;

  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  @KafkaListener(topics = "users", groupId = "userdata")
  public void listener(final @Payload UserJson user,final  ConsumerRecord<String, UserJson> cr) {
    userRepository.findByUsername(user.username())
        .ifPresentOrElse(
            u -> log.info("### User already exist in DB, kafka event will be skipped: {}", cr),
            () -> {
              log.info("### Kafka consumer record: {}", cr);

              UserEntity ue = new UserEntity();
              ue.setUsername(user.username());
              ue.setCountry(DEFAULT_COUNTRY);
              UserEntity userEntity = userRepository.save(ue);

              log.info(
                  "### User '{}' successfully saved to database with id: {}",
                  user.username(),
                  userEntity.getId()
              );
            }
        );
  }

  @Transactional(readOnly = true)
  public @Nonnull UserJson getCurrentUser(final String username) {
    return userRepository.findByUsername(username).map(UserJson::fromEntity)
        .orElseGet(() -> new UserJson(
            null,
            username,
            null,
            null,
            DEFAULT_COUNTRY,
            null,
            null
        ));
  }

  @Transactional
  public @Nonnull UserJson update(final UserRequest user) {
    UserEntity ue = userRepository.findByUsername(user.getUsername())
        .orElseGet(() -> {
              UserEntity emptyUser = new UserEntity();
              emptyUser.setUsername(user.getUsername());
              emptyUser.setCountry(DEFAULT_COUNTRY);
              return emptyUser;
            }
        );
    ue.setCountry(
        user.getCountry().isEmpty()
            ? DEFAULT_COUNTRY
            : CountryValues.valueOf(user.getCountry().toUpperCase())
    );
    if (isAvatarString(user.getAvatar())) {
      ue.setAvatar(
          user.getAvatar().isEmpty()
              ? null
              : new SmallAvatar(220, 220, user.getAvatar()).bytes()
      );
    }
    ue.setFirstname(user.getFirstname().isEmpty() ? null : user.getFirstname());
    ue.setSurname(user.getSurname().isEmpty() ? null : user.getSurname());
    return UserJson.fromEntity(userRepository.save(ue));
  }

  @Transactional(readOnly = true)
  public @Nonnull Page<UserJson> allUsers(
      final String username,
      final Pageable pageable,
      final @Nullable String searchQuery) {
    final Page<UserWithStatus> usersFromDb = searchQuery == null
        ? userRepository.findByUsernameNot(username, pageable)
        : userRepository.findByUsernameNotAndSearchQuery(username, searchQuery, pageable);
    return usersFromDb.map(UserJson::fromUserEntityProjection);
  }

  @Transactional(readOnly = true)
  public @Nonnull Page<UserJson> friends(
      final String username,
      final Pageable pageable,
      final @Nullable String searchQuery) {
    final Page<UserWithStatus> usersFromDb = searchQuery == null
        ? userRepository.findFriends(getRequiredUser(username), pageable)
        : userRepository.findFriends(getRequiredUser(username), pageable, searchQuery);
    return usersFromDb.map(UserJson::fromUserEntityProjection);
  }

  @Transactional(readOnly = true)
  public @Nonnull List<String> friendsIds(final String username) {
    final List<UserWithStatus> usersFromDb = userRepository.findFriends(getRequiredUser(username));
    return usersFromDb.stream()
        .map(UserWithStatus::id)
        .map(UUID::toString)
        .toList();
  }

  @Transactional(readOnly = true)
  public @Nonnull Page<UserJson> outcomeInvitations(
      final String username,
      final Pageable pageable,
      final @Nullable String searchQuery) {
    final Page<UserWithStatus> usersFromDb = searchQuery == null
        ? userRepository.findOutcomeInvitations(getRequiredUser(username), pageable)
        : userRepository.findOutcomeInvitations(getRequiredUser(username), pageable, searchQuery);
    return usersFromDb.map(UserJson::fromUserEntityProjection);
  }

  @Transactional(readOnly = true)
  public @Nonnull Page<UserJson> incomeInvitations(
      final String username,
      final PageRequest pageable,
      final @Nullable String searchQuery) {
    final Page<UserWithStatus> usersFromDb = searchQuery == null
        ? userRepository.findIncomeInvitations(getRequiredUser(username), pageable)
        : userRepository.findIncomeInvitations(getRequiredUser(username), pageable, searchQuery);
    return usersFromDb.map(UserJson::fromUserEntityProjection);
  }

  @Transactional
  public UserJson createFriendshipRequest(final String username, final String targetUserId) {
    if (Objects.equals(username, targetUserId)) {
      throw new SameUsernameException("Can`t create friendship request for self user");
    }
    final UserEntity currentUser = getRequiredUser(username);
    final UserEntity targetUser = getRequiredUser(UUID.fromString(targetUserId));
    final guru.qa.rangiffler.model.FriendshipStatus returnedStates;
    final Optional<FriendshipEntity> mayBeInvite = getFriendshipRequest(currentUser, targetUser);
    if (mayBeInvite.isPresent()) {
      mayBeInvite.get().setStatus(FriendshipStatus.ACCEPTED);
      currentUser.addFriends(FriendshipStatus.ACCEPTED, targetUser);
      returnedStates = FRIEND;
    } else {
      currentUser.addFriends(FriendshipStatus.PENDING, targetUser);
      returnedStates = INVITATION_SENT;
    }
    userRepository.save(currentUser);
    return UserJson.fromEntity(targetUser, returnedStates);
  }

  @Transactional
  public @Nonnull UserJson acceptFriendshipRequest(final String username, final String userId) {
    if (Objects.equals(username, userId)) {
      throw new SameUsernameException("Can`t accept friendship request for self user");
    }
    final UserEntity currentUser = getRequiredUser(username);
    final UserEntity targetUser = getRequiredUser(UUID.fromString(userId));

    final FriendshipEntity invite = getFriendshipRequest(currentUser, targetUser)
        .orElseThrow(() -> new NotFoundException("Can`t find invitation from username: '" + userId + "'"));

    invite.setStatus(FriendshipStatus.ACCEPTED);
    currentUser.addFriends(FriendshipStatus.ACCEPTED, targetUser);
    userRepository.save(currentUser);
    return UserJson.fromEntity(targetUser, FRIEND);
  }

  @Transactional
  public @Nonnull UserJson declineFriendshipRequest(final String username,final  String userId) {
    if (Objects.equals(username, userId)) {
      throw new SameUsernameException("Can`t decline friendship request for self user");
    }
    final UserEntity currentUser = getRequiredUser(username);
    final UserEntity targetUser = getRequiredUser(UUID.fromString(userId));

    currentUser.removeInvites(targetUser);
    targetUser.removeFriends(currentUser);

    userRepository.save(currentUser);
    userRepository.save(targetUser);
    return UserJson.fromEntity(targetUser);
  }

  @Transactional
  public UserJson removeFriend(final String username,final  String userId) {
    if (Objects.equals(username, userId)) {
      throw new SameUsernameException("Can`t remove friendship relation for self user");
    }
    final UserEntity currentUser = getRequiredUser(username);
    final UserEntity targetUser = getRequiredUser(UUID.fromString(userId));

    currentUser.removeFriends(targetUser);
    currentUser.removeInvites(targetUser);
    targetUser.removeFriends(currentUser);
    targetUser.removeInvites(currentUser);

    userRepository.save(currentUser);
    userRepository.save(targetUser);
    return UserJson.fromEntity(targetUser);
  }

  private boolean isAvatarString(final @Nullable String photo) {
    return photo != null && photo.startsWith("data:image");
  }

  @Nonnull
  private UserEntity getRequiredUser(final String username) {
    return userRepository.findByUsername(username).orElseThrow(
        () -> new NotFoundException("Can`t find user by username: '" + username + "'")
    );
  }

  @Nonnull
  private UserEntity getRequiredUser(final UUID id) {
    return userRepository.findById(id).orElseThrow(
        () -> new NotFoundException("Can`t find user by username: '" + id + "'")
    );
  }

  @Nonnull
  private Optional<FriendshipEntity> getFriendshipRequest(final UserEntity currentUser, final UserEntity targetUser) {
    return currentUser.getFriendshipAddressees()
        .stream()
        .filter(fe -> fe.getRequester().equals(targetUser))
        .findFirst();
  }
}
