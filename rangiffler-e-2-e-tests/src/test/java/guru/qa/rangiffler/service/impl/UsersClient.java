package guru.qa.rangiffler.service.impl;

import guru.qa.FriendshipActionMutation;
import guru.qa.FriendshipActionMutation.Friendship;
import guru.qa.GetFriendsQuery;
import guru.qa.GetInvitationsQuery;
import guru.qa.GetOutcomeInvitationsQuery;
import guru.qa.GetUserQuery;
import guru.qa.rangiffler.helpers.RandomDataUtils;
import guru.qa.rangiffler.jupiter.annotation.Photo;
import guru.qa.rangiffler.jupiter.extension.UserExtension;
import guru.qa.rangiffler.model.CountryJson;
import guru.qa.rangiffler.model.FriendshipJson;
import guru.qa.rangiffler.model.PhotoJson;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.type.FriendshipAction;
import guru.qa.type.FriendshipInput;
import io.qameta.allure.Step;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;

@ParametersAreNonnullByDefault
public final class UsersClient extends GraphQLClient {

  private final String token;
  @Getter
  private final List<PhotoJson> createdPhotos = new ArrayList<>();

  public UsersClient(String token) {
    this.token = token;
  }

  @Nonnull
  @Step("Создание пользователя")
  public static UsersClient create(String username, @Nullable Photo[] photos) {
    AuthApiClient authClient = new AuthApiClient(null);
    authClient.register(username, UserExtension.DEFAULT_PASSWORD);
    final String token = authClient.login(username, UserExtension.DEFAULT_PASSWORD);
    return new UsersClient(token).createPhotos(photos);
  }

  public UsersClient createPhotos(@Nullable Photo[] photos) {
    if (photos != null) {
      List<PhotoJson> result = Stream.of(photos)
          .map(photo ->
              new PhotoGraphQlClient(token)
                  .createPhoto(photo.img(), photo.country(), photo.description())
          )
          .map(PhotoJson::fromGraphQlCreatedPhoto)
          .toList();
      createdPhotos.addAll(result);
    }
    return this;
  }

  @Nonnull
  @Step("Получаем данные пользователя")
  public UserJson currentUser() {
    GetUserQuery.User userGql = response(new GetUserQuery(), token).user;
    return new UserJson(
        UUID.fromString(userGql.id),
        userGql.username,
        userGql.firstname,
        userGql.surname,
        userGql.avatar,
        new CountryJson(
            userGql.location.code,
            userGql.location.name,
            null
        ),
        null,
        null
    );
  }

  @Nonnull
  @Step("Получаем друзей")
  public List<UserJson> friendsQuery(Integer page, Integer size, String searchQuery) {
    GetFriendsQuery friendsQuery = GetFriendsQuery.builder()
        .page(page)
        .size(size)
        .searchQuery(searchQuery)
        .build();
    return response(friendsQuery, token)
        .user
        .friends
        .edges.stream()
        .map(e -> e.node)
        .map(userGql -> new UserJson(
                UUID.fromString(userGql.id),
                userGql.username,
                userGql.firstname,
                userGql.surname,
                userGql.avatar,
                new CountryJson(
                    userGql.location.code,
                    userGql.location.name,
                    userGql.location.flag
                ),
                userGql.friendStatus,
                null
            )
        )
        .toList();
  }

  @Nonnull
  @Step("Получаем Входящие запросы дружбы")
  public List<UserJson> invitationsQuery(Integer page, Integer size, String searchQuery) {
    GetInvitationsQuery invitationsQuery = GetInvitationsQuery.builder()
        .page(page)
        .size(size)
        .searchQuery(searchQuery)
        .build();
    return response(invitationsQuery, token)
        .user
        .incomeInvitations
        .edges.stream()
        .map(e -> e.node)
        .map(userGql -> new UserJson(
                UUID.fromString(userGql.id),
                userGql.username,
                userGql.firstname,
                userGql.surname,
                userGql.avatar,
                new CountryJson(
                    userGql.location.code,
                    userGql.location.name,
                    userGql.location.flag
                ),
                userGql.friendStatus,
                null
            )
        )
        .toList();
  }

  @Nonnull
  @Step("Получаем Исходящие запросы дружбы")
  public List<UserJson> outcomeInvitationsQuery(Integer page, Integer size, String searchQuery) {
    GetOutcomeInvitationsQuery outcomeInvitationsQuery = GetOutcomeInvitationsQuery.builder()
        .page(page)
        .size(size)
        .searchQuery(searchQuery)
        .build();
    return response(outcomeInvitationsQuery, token)
        .user
        .outcomeInvitations
        .edges.stream()
        .map(e -> e.node)
        .map(userGql -> new UserJson(
                UUID.fromString(userGql.id),
                userGql.username,
                userGql.firstname,
                userGql.surname,
                userGql.avatar,
                new CountryJson(
                    userGql.location.code,
                    userGql.location.name,
                    userGql.location.flag
                ),
                userGql.friendStatus,
                null
            )
        )
        .toList();
  }

  @Nonnull
  @Step("Получаем: входящие запросы дружбы")
  public List<UserJson> createIncomeInvitation(UserJson user, int count, @Nullable Photo[] photos) {
    final List<UsersClient> result = IntStream.range(0, count)
        .mapToObj(i -> create(RandomDataUtils.getRandomUserName(), photos))
        .toList();
    result.forEach(u -> u.sendInvitation(user));
    return result.stream().map(UsersClient::currentUser).toList();
  }

  @Nonnull
  @Step("Получаем: исходящие запросы дружбы")
  public List<UserJson> createOutcomeInvitation(int count) {
    final List<UserJson> result = IntStream.range(0, count)
        .mapToObj(i -> create(RandomDataUtils.getRandomUserName(), null))
        .map(UsersClient::currentUser)
        .toList();
    result.forEach(this::sendInvitation);
    return result;
  }

  @Nonnull
  @Step("Создание друзей")
  public List<UserJson> createFriends(UserJson user, int count, @Nullable Photo[] photos) {
    final List<UserJson> result = createIncomeInvitation(user, count, photos);
    result.forEach(this::acceptFriend);
    return result;
  }

  @Nonnull
  private FriendshipJson friendshipMutation(UserJson targetUser, FriendshipAction action) {
    FriendshipActionMutation friendshipMutation = FriendshipActionMutation.builder()
        .input(
            FriendshipInput.builder()
                .user(targetUser.getId().toString())
                .action(action)
                .build()
        )
        .build();
    final Friendship friendship = response(friendshipMutation, token).friendship;
    return new FriendshipJson(
        UUID.fromString(friendship.id),
        friendship.username,
        friendship.friendStatus
    );
  }

  @Nonnull
  private FriendshipJson acceptFriend(UserJson requester) {
    return friendshipMutation(requester, FriendshipAction.ACCEPT);
  }

  @Nonnull
  public FriendshipJson sendInvitation(UserJson requester) {
    return friendshipMutation(requester, FriendshipAction.ADD);
  }
}
