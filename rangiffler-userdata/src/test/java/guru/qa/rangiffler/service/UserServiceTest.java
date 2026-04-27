package guru.qa.rangiffler.service;

import static guru.qa.rangiffler.data.FriendshipStatus.ACCEPTED;
import static guru.qa.rangiffler.data.FriendshipStatus.PENDING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import guru.qa.rangiffler.data.CountryValues;
import guru.qa.rangiffler.data.FriendshipEntity;
import guru.qa.rangiffler.data.UserEntity;
import guru.qa.rangiffler.data.projection.UserWithStatus;
import guru.qa.rangiffler.data.repository.UserRepository;
import guru.qa.rangiffler.ex.NotFoundException;
import guru.qa.rangiffler.ex.SameUsernameException;
import guru.qa.rangiffler.grpc.FriendshipStatus;
import guru.qa.rangiffler.grpc.UserRequest;
import guru.qa.rangiffler.grpc.UserResponse;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.util.ByteAsString;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  private UserService userService;

  private List<UserEntity> users = new ArrayList<>();

  private UserEntity mainTestUser;
  private UserEntity secondTestUser;
  private UserEntity thirdTestUser;
  private UserEntity fourthTestUser;
  private UserEntity fifthTestUser;

  private final String mainTestUserName = "mainUser";
  private final UUID secondTestUserId = UUID.randomUUID();
  private final String errorMsgTemplate = "Can`t %s friendship request for self user";

  @BeforeEach
  void before() {
    userService = new UserService(userRepository);
    mainTestUser = new UserEntity();
    mainTestUser.setId(UUID.randomUUID());
    mainTestUser.setUsername(mainTestUserName);
    mainTestUser.setFirstname("firstname");
    mainTestUser.setSurname("surname");
    mainTestUser.setCountry(CountryValues.RU);
    users.add(mainTestUser);

    secondTestUser = new UserEntity();
    secondTestUser.setId(secondTestUserId);
    secondTestUser.setUsername("second");
    secondTestUser.setFirstname("secondFirstname");
    secondTestUser.setSurname("secondSurname");
    secondTestUser.setCountry(CountryValues.US);
    users.add(secondTestUser);

    thirdTestUser = new UserEntity();
    thirdTestUser.setId(UUID.randomUUID());
    thirdTestUser.setUsername("third");
    thirdTestUser.setFirstname("thirdFirstname");
    thirdTestUser.setSurname("thirdSurname");
    thirdTestUser.setCountry(CountryValues.KZ);
    users.add(thirdTestUser);

    fourthTestUser = new UserEntity();
    fourthTestUser.setId(UUID.randomUUID());
    fourthTestUser.setUsername("fourth");
    fourthTestUser.setFirstname("fourthFirstname");
    fourthTestUser.setSurname("fourthSurname");
    fourthTestUser.setCountry(CountryValues.TG);
    users.add(fourthTestUser);

    fifthTestUser = new UserEntity();
    fifthTestUser.setId(UUID.randomUUID());
    fifthTestUser.setUsername("fifth");
    fifthTestUser.setFirstname("fifthFirstname");
    fifthTestUser.setSurname("fifthSurname");
    fifthTestUser.setCountry(CountryValues.TG);
    users.add(fifthTestUser);
  }

  @Test
  void listenerShouldSaveExistUser(
      @Mock ConsumerRecord<String, UserJson> cr
  ) {
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));

    final UserJson userJson = userJsonFromEntity(mainTestUser);

    userService = new UserService(userRepository);

    userService.listener(userJson, cr);
    then(userRepository).should(times(0)).save(any(UserEntity.class));
  }

  @Test
  void listenerShouldSaveIfUserNotExist(
      @Mock ConsumerRecord<String, UserJson> cr
  ) {
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.empty());
    when(userRepository.save(any(UserEntity.class)))
        .thenAnswer(answer -> answer.getArguments()[0]);
    final UserJson userJson = userJsonFromEntity(mainTestUser);

    userService = new UserService(userRepository);

    userService.listener(userJson, cr);
    then(userRepository).should(times(1)).save(any(UserEntity.class));
  }

  @Test
  void currentUserShouldReturnUserResponse() {
    when(userRepository.findByUsername(eq(mainTestUserName))).thenReturn(Optional.of(mainTestUser));
    UserJson actual = userService.getCurrentUser(mainTestUserName);

    verify(userRepository).findByUsername(eq(mainTestUserName));
    Assertions.assertThat(actual)
        .isEqualTo(userJsonFromEntity(mainTestUser));
  }

  @ValueSource(strings = {
      "data:image/png;base64,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",
      ""
  })
  @ParameterizedTest
  void userShouldBeUpdated(String photo) {
    byte[] photoForTest = photo.isEmpty() ? "".getBytes() : toSmallAvatar(photo);
    mainTestUser.setAvatar(photoForTest);
    when(userRepository.findByUsername(eq(mainTestUserName))).thenReturn(Optional.of(mainTestUser));
    when(userRepository.save(eq(mainTestUser))).thenReturn(mainTestUser);
    UserJson userJson = userJsonFromEntity(mainTestUser);
    UserJson actual = userService.update(userRequestFromUserEntity(mainTestUser));

    verify(userRepository).findByUsername(eq(mainTestUserName));
    verify(userRepository).save(eq(mainTestUser));

    Assertions.assertThat(actual)
        .isEqualTo(userJson);
  }

  @Test
  void updateShouldBeUpdatedAndIgnoreWrongPhoto() {
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));
    when(userRepository.save(any(UserEntity.class)))
        .thenAnswer(answer -> answer.getArguments()[0]);

    final UserRequest request = UserRequest.newBuilder()
        .setUsername(mainTestUser.getUsername())
        .setFirstname("TestFirstname")
        .setSurname("TestSurname")
        .setAvatar("not data:image")
        .setCountry(CountryValues.AG.getCode())
        .build();

    final UserJson result = userService.update(request);
    final UserJson expected = new UserJson(
        mainTestUser.getId(),
        request.getUsername(),
        request.getFirstname(),
        request.getSurname(),
        CountryValues.valueOf(request.getCountry().toUpperCase()),
        "",
        null
    );

    verify(userRepository).save(any(UserEntity.class));
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void updateShouldSaveNotExistUserWitDefaultCountry() {
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.empty());
    when(userRepository.save(any(UserEntity.class)))
        .thenAnswer(answer -> answer.getArguments()[0]);
    UserRequest request = UserRequest.newBuilder()
        .setUsername(mainTestUser.getUsername())
        .setFirstname("TestFirstname")
        .setSurname("TestSurname")
        .build();

    final UserJson result = userService.update(request);
    final UserJson expected = new UserJson(
        result.id(),
        request.getUsername(),
        request.getFirstname(),
        request.getSurname(),
        CountryValues.RU,
        "",
        null
    );

    assertThat(result).isEqualTo(expected);
    verify(userRepository).save(any(UserEntity.class));
  }

  @Test
  void getRequiredUserShouldThrowNotFoundExceptionIfUserNotFound() {
    final String notExistingUser = "notExist";
    when(userRepository.findByUsername(eq(notExistingUser)))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.getRequiredUser(notExistingUser))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Can`t find user by username: '" + notExistingUser + "'")
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  void allUsersShouldReturnCorrectUsersList() {
    final Pageable pageable = PageRequest.of(1, 1);
    when(userRepository.findByUsernameNot(eq(mainTestUserName), eq(pageable)))
        .thenReturn(getMockUsersMappingFromDb());

    final Page<UserJson> users = userService.allUsers(mainTestUserName, pageable, null);
    assertThat(users).hasSize(4);
    final UserJson invitationReceivedUser = users.stream()
        .filter(u -> u.friendshipStatus() == guru.qa.rangiffler.model.FriendshipStatus.INVITATION_RECEIVED)
        .findFirst()
        .orElseThrow(() -> new AssertionError("Friend with state INVITATION_RECEIVED not found"));

    final UserJson friend = users.stream()
        .filter(u -> u.friendshipStatus() == guru.qa.rangiffler.model.FriendshipStatus.FRIEND)
        .findFirst()
        .orElseThrow(() -> new AssertionError("user without status not found"));

    final UserJson invitationSentUser = users.stream()
        .filter(u -> u.friendshipStatus() == guru.qa.rangiffler.model.FriendshipStatus.INVITATION_SENT)
        .findFirst()
        .orElseThrow(() -> new AssertionError("Friend with state INVITE_SENT not found"));

    final UserJson withoutFriendshipUser = users.stream()
        .filter(u -> u.friendshipStatus() == null)
        .findFirst()
        .orElseThrow(() -> new AssertionError("Friend with state null not found"));

    assertThat(secondTestUser.getUsername()).isEqualTo(invitationReceivedUser.username());
    assertThat(thirdTestUser.getUsername()).isEqualTo(friend.username());
    assertThat(fourthTestUser.getUsername()).isEqualTo(invitationSentUser.username());
    assertThat(fifthTestUser.getUsername()).isEqualTo(withoutFriendshipUser.username());
  }

  @Test
  void getCurrentUserShouldReturnUserIfNotExistInDb() {
    UserJson expected = new UserJson(
        null,
        "notExistingUser",
        null,
        null,
        CountryValues.RU,
        null,
        null
    );
    when(userRepository.findByUsername(eq(expected.username())))
        .thenReturn(Optional.empty());

    final UserJson actual = userService.getCurrentUser(expected.username());

    assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  void allUsersShouldReturnUserWithSmallPhoto() {
    final Pageable pageable = PageRequest.of(1, 1);
    final UserJson expected = new UserJson(
        mainTestUser.getId(),
        mainTestUser.getUsername(),
        "firstname",
        mainTestUser.getSurname(),
        CountryValues.AG,
        "data:image/png;base64,R0lGODlhAQABAIAAAP///wAAACwAAAAAAQABAAACAkQBADs=",
        guru.qa.rangiffler.model.FriendshipStatus.INVITATION_SENT
    );

    when(userRepository.findByUsernameNot(eq(expected.username()), eq(pageable)))
        .thenReturn(new PageImpl<>(List.of(
            new UserWithStatus(
                Objects.requireNonNull(expected.id()),
                expected.username(),
                expected.firstname(),
                expected.surname(),
                expected.country(),
                Objects.requireNonNull(expected.avatar()).getBytes(),
                PENDING,
                null,
                expected.id()
            )
        )));

    Page<UserJson> actual = userService.allUsers(mainTestUser.getUsername(), pageable, null);
    assertThat(actual.getContent())
        .hasSize(1)
        .containsExactly(expected);
  }

  @Test
  void allUsersShouldCallExpectedMethodWithSearchQuery() {
    final String searchQuery = "test";
    final Pageable pageable = PageRequest.of(1, 1);
    when(userRepository.findByUsernameNotAndSearchQuery(eq(mainTestUserName), eq(searchQuery), eq(pageable)))
        .thenReturn(new PageImpl<>(List.of()));

    userService.allUsers(mainTestUserName, pageable, searchQuery);
    verify(userRepository).findByUsernameNotAndSearchQuery(mainTestUserName, searchQuery, pageable);
  }

  @Test
  void fiendsPageShouldCallExpectedMethodWithSearchQuery() {
    final String searchQuery = "test";
    final Pageable pageable = PageRequest.of(1, 1);
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));
    when(userRepository.findFriends(eq(mainTestUser), eq(pageable), eq(searchQuery)))
        .thenReturn(new PageImpl<>(List.of()));

    userService.friends(mainTestUserName, pageable, searchQuery);
    then(userRepository).should(times(1))
        .findFriends(mainTestUser, pageable, searchQuery);
  }

  @Test
  void fiendsPageShouldCallExpectedMethodWithoutSearchQuery() {
    final Pageable pageable = PageRequest.of(1, 1);
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));
    when(userRepository.findFriends(eq(mainTestUser), eq(pageable)))
        .thenReturn(new PageImpl<>(List.of()));

    userService.friends(mainTestUserName, pageable, null);
    then(userRepository).should(times(1))
        .findFriends(mainTestUser, pageable);
  }

  @Test
  void friendsIdsShouldCallExpectedMethod() {
    final List<UserWithStatus> users = getMockUsersMappingFromDb().getContent();
    final List<UUID> expected = users.stream().map(UserWithStatus::id).toList();
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));
    when(userRepository.findFriends(eq(mainTestUser))).thenReturn(users);

    Stream<UUID> actual = userService.friendsIds(mainTestUserName);
    verify(userRepository).findByUsername(mainTestUserName);
    verify(userRepository).findFriends(mainTestUser);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void createFriendshipRequestShouldCreateFriend() {
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));
    when(userRepository.findById(eq(secondTestUserId)))
        .thenReturn(Optional.of(secondTestUser));
    when(userRepository.save(any(UserEntity.class)))
        .thenAnswer(answer -> answer.getArguments()[0]);

    addFriendshipBetweenMainAndSecondUsers();

    UserJson user = userService.createFriendshipRequest(mainTestUserName, secondTestUserId.toString());
    verify(userRepository).findByUsername(mainTestUserName);
    verify(userRepository).findById(secondTestUserId);
    assertThat(user)
        .isEqualTo(UserJson.fromEntity(secondTestUser, guru.qa.rangiffler.model.FriendshipStatus.FRIEND));
    verify(userRepository).save(mainTestUser);
  }

  @Test
  void createFriendshipRequestShouldInviteSent() {
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));
    when(userRepository.findById(eq(secondTestUserId)))
        .thenReturn(Optional.of(secondTestUser));
    when(userRepository.save(any(UserEntity.class)))
        .thenAnswer(answer -> answer.getArguments()[0]);

    UserJson user = userService.createFriendshipRequest(mainTestUserName, secondTestUserId.toString());
    assertThat(user)
        .isEqualTo(UserJson.fromEntity(secondTestUser, guru.qa.rangiffler.model.FriendshipStatus.INVITATION_SENT));
    verify(userRepository).findByUsername(mainTestUserName);
    verify(userRepository).findById(secondTestUserId);
    verify(userRepository).save(mainTestUser);
    verify(userRepository).save(any());
  }

  @Test
  void createFriendshipRequestShouldThrowSameUsernameException() {
    final UUID mainTestUserUUID = mainTestUser.getId();
    when(userRepository.findById(eq(mainTestUserUUID))).thenReturn(Optional.of(mainTestUser));
    assertThatThrownBy(() ->
        userService.createFriendshipRequest(
            mainTestUser.getUsername(), mainTestUser.getId().toString()
        ))
        .isInstanceOf(SameUsernameException.class)
        .hasMessage(errorMsgTemplate.formatted("create"));
    verify(userRepository).findById(mainTestUserUUID);
  }

  @Test
  void acceptFriendshipRequestShouldThrowSameUsernameException() {
    final UUID mainTestUserUUID = mainTestUser.getId();
    when(userRepository.findById(eq(mainTestUserUUID))).thenReturn(Optional.of(mainTestUser));
    assertThatThrownBy(() -> userService.acceptFriendshipRequest(mainTestUserName, mainTestUserUUID.toString()))
        .isInstanceOf(SameUsernameException.class)
        .hasMessage(errorMsgTemplate.formatted("accept"));
    verify(userRepository).findById(mainTestUserUUID);
  }

  @Test
  void acceptFriendshipRequestShouldThrowNotFoundException() {
    FriendshipEntity addressee = new FriendshipEntity();
    addressee.setRequester(new UserEntity());
    mainTestUser.setFriendshipAddressees(List.of(addressee));
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));
    when(userRepository.findById(eq(secondTestUserId)))
        .thenReturn(Optional.of(secondTestUser));

    assertThatThrownBy(() -> userService.acceptFriendshipRequest(mainTestUserName, secondTestUserId.toString()))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Can`t find invitation from  target userId: '%s'".formatted(secondTestUser.getId()));
  }

  @Test
  void acceptFriendshipRequestShouldBeSuccess() {
    addFriendshipBetweenMainAndSecondUsers();

    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));
    when(userRepository.findById(eq(secondTestUserId)))
        .thenReturn(Optional.of(secondTestUser));

    UserJson user = userService.acceptFriendshipRequest(mainTestUserName, secondTestUserId.toString());
    assertThat(user)
        .isEqualTo(UserJson.fromEntity(secondTestUser, guru.qa.rangiffler.model.FriendshipStatus.FRIEND));
    then(userRepository).should(times(1)).save(eq(mainTestUser));
  }

  @Test
  void declineFriendshipRequestShouldThrowSameUsernameException() {
    final UUID mainTestUserUUID = mainTestUser.getId();
    when(userRepository.findById(eq(mainTestUserUUID))).thenReturn(Optional.of(mainTestUser));
    assertThatThrownBy(() -> userService.declineFriendshipRequest(mainTestUserName, mainTestUserUUID.toString()))
        .isInstanceOf(SameUsernameException.class)
        .hasMessage(errorMsgTemplate.formatted("decline"));
    verify(userRepository).findById(mainTestUserUUID);
  }

  @Test
  void declineFriendshipRequestShouldBeSuccess() {
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));
    when(userRepository.findById(eq(secondTestUserId)))
        .thenReturn(Optional.of(secondTestUser));
    addFriendshipBetweenMainAndSecondUsers();

    System.out.println(mainTestUser);
    System.out.println(secondTestUser);

    UserJson result = userService.declineFriendshipRequest(mainTestUserName, secondTestUserId.toString());

    assertThat(mainTestUser)
        .describedAs("Инициатор. Удалено предложение дружбы")
        .matches(u -> u.getFriendshipAddressees().isEmpty());
    assertThat(secondTestUser)
        .describedAs("Реципиент. Удалено предложение дружбы")
        .matches(u -> u.getFriendshipAddressees().isEmpty());
    then(userRepository).should(times(1)).save(secondTestUser);
    then(userRepository).should(times(1)).save(mainTestUser);
    assertThat(result)
        .isEqualTo(UserJson.fromEntity(secondTestUser));
  }

  @Test
  void removeFriendShouldThrowSameUsernameException() {
    final UUID mainTestUserUUID = mainTestUser.getId();
    when(userRepository.findById(eq(mainTestUserUUID))).thenReturn(Optional.of(mainTestUser));
    assertThatThrownBy(() -> userService.removeFriend(mainTestUserName, mainTestUserUUID.toString()))
        .isInstanceOf(SameUsernameException.class)
        .hasMessage(errorMsgTemplate.formatted("remove"));
    verify(userRepository).findById(mainTestUserUUID);
  }

  @Test
  void removeFriendShouldUseSave() {
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));
    when(userRepository.findById(eq(secondTestUserId)))
        .thenReturn(Optional.of(secondTestUser));
    addFriendshipBetweenMainAndSecondUsers();

    userService.removeFriend(mainTestUserName, secondTestUserId.toString());
    then(userRepository).should(times(1)).save(secondTestUser);
    then(userRepository).should(times(1)).save(mainTestUser);
    Stream.of(mainTestUser, secondTestUser)
        .forEach(user ->
            assertThat(user)
                .describedAs("%s. Удалён друг".formatted(user.getUsername()))
                .matches(u -> u.getFriendshipAddressees().isEmpty())
                .matches(u -> u.getFriendshipRequests().isEmpty())
        );
  }

  private UserJson userJsonFromEntity(UserEntity ue) {
    return new UserJson(
        ue.getId(),
        ue.getUsername(),
        ue.getFirstname(),
        ue.getSurname(),
        ue.getCountry(),
        ue.getAvatar() != null && ue.getAvatar().length > 0
            ? new ByteAsString(ue.getAvatar()).string()
            : "",
        null
    );
  }

  private UserRequest userRequestFromUserEntity(UserEntity ue) {
    return UserRequest.newBuilder()
        .setUsername(ue.getUsername())
        .setFirstname(ue.getFirstname())
        .setSurname(ue.getSurname())
        .setAvatar(new String(ue.getAvatar(), StandardCharsets.UTF_8))
        .setCountry(ue.getCountry().getCode())
        .build();
  }

  private byte[] toSmallAvatar(@Nullable String photo) {
    return new SmallAvatar(220, 220, photo).bytes();
  }

  private UserResponse userResponseFromEntity(UserEntity user, FriendshipStatus friendshipStatus) {
    return UserResponse.newBuilder()
        .setId(user.getId() == null ? "" : user.getId().toString())
        .setUsername(user.getUsername())
        .setFirstname(user.getFirstname() == null ? "" : user.getFirstname())
        .setSurname(user.getSurname() == null ? "" : user.getSurname())
        .setAvatar(user.getAvatar() == null ? "" : new String(user.getAvatar(), StandardCharsets.UTF_8))
        .setCountry(guru.qa.rangiffler.grpc.CountryValues.valueOf(user.getCountry().name()))
        .setFriendshipStatus(friendshipStatus)
        .build();
  }

  private Page<UserWithStatus> getMockUsersMappingFromDb() {
    return new PageImpl<>(List.of(
        new UserWithStatus(
            secondTestUser.getId(),
            secondTestUser.getUsername(),
            secondTestUser.getFirstname(),
            secondTestUser.getSurname(),
            secondTestUser.getCountry(),
            secondTestUser.getAvatar(),
            PENDING,
            secondTestUser.getId(),
            mainTestUser.getId()
        ),
        new UserWithStatus(
            thirdTestUser.getId(),
            thirdTestUser.getUsername(),
            thirdTestUser.getFirstname(),
            thirdTestUser.getSurname(),
            thirdTestUser.getCountry(),
            thirdTestUser.getAvatar(),
            ACCEPTED,
            mainTestUser.getId(),
            thirdTestUser.getId()
        ),
        new UserWithStatus(
            fourthTestUser.getId(),
            fourthTestUser.getUsername(),
            fourthTestUser.getFirstname(),
            fourthTestUser.getSurname(),
            fourthTestUser.getCountry(),
            fourthTestUser.getAvatar(),
            PENDING,
            mainTestUser.getId(),
            fourthTestUser.getId()
        ),
        new UserWithStatus(
            fifthTestUser.getId(),
            fifthTestUser.getUsername(),
            fifthTestUser.getFirstname(),
            fifthTestUser.getSurname(),
            fifthTestUser.getCountry(),
            fifthTestUser.getAvatar(),
            PENDING,
            null,
            null
        )
    ));
  }

  private void addFriendshipBetweenMainAndSecondUsers() {
    FriendshipEntity mainUserFriend = new FriendshipEntity();
    mainUserFriend.setRequester(secondTestUser);
    mainTestUser.getFriendshipAddressees().add(mainUserFriend);
    FriendshipEntity secondUserFriend = new FriendshipEntity();
    secondUserFriend.setAddressee(mainTestUser);
    secondTestUser.getFriendshipRequests().add(secondUserFriend);
  }
}
