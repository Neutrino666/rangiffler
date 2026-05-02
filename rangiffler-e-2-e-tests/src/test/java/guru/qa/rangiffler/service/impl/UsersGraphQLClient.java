package guru.qa.rangiffler.service.impl;

import guru.qa.FriendshipActionMutation;
import guru.qa.FriendshipActionMutation.Friendship;
import guru.qa.GetFriendsQuery;
import guru.qa.GetInvitationsQuery;
import guru.qa.GetOutcomeInvitationsQuery;
import guru.qa.GetPeopleQuery;
import guru.qa.GetUserQuery;
import guru.qa.GetUserQuery.User;
import guru.qa.rangiffler.helpers.RandomDataUtils;
import guru.qa.rangiffler.model.TestPrefix;
import guru.qa.rangiffler.jupiter.extension.UserExtension;
import guru.qa.rangiffler.model.FriendshipJson;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.type.FriendshipAction;
import guru.qa.type.FriendshipInput;
import io.qameta.allure.Step;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class UsersGraphQLClient extends GraphQLClient {

  private final String token;

  public UsersGraphQLClient(String token) {
    this.token = token;
  }

  @Nonnull
  @Step(TestPrefix.GRAPHQL + "Текущий пользователь")
  public UserJson currentUser() {
    User userGql = response(new GetUserQuery(), token).user;
    return new UserJson(
        UUID.fromString(userGql.id),
        userGql.username,
        userGql.firstname,
        userGql.surname,
        userGql.avatar,
        userGql.location.code,
        null,
        null
    );
  }

  @Nonnull
  @Step(TestPrefix.GRAPHQL + "Друзья")
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
                userGql.location.code,
                userGql.friendStatus,
                null
            )
        )
        .toList();
  }

  @Nonnull
  @Step(TestPrefix.GRAPHQL + "Входящие запросы дружбы")
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
                userGql.location.code,
                userGql.friendStatus,
                null
            )
        )
        .toList();
  }

  @Nonnull
  @Step(TestPrefix.GRAPHQL + "Исходящие запросы дружбы")
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
                userGql.location.code,
                userGql.friendStatus,
                null
            )
        )
        .toList();
  }

  @Nonnull
  @Step(TestPrefix.GRAPHQL + "Все пользователи")
  public List<UserJson> peopleQuery(Integer page, Integer size, String searchQuery) {
    GetPeopleQuery peopleQuery = GetPeopleQuery.builder()
        .page(page)
        .size(size)
        .searchQuery(searchQuery)
        .build();
    return response(peopleQuery, token)
        .users
        .edges.stream()
        .map(e -> e.node)
        .map(userGql -> new UserJson(
                UUID.fromString(userGql.id),
                userGql.username,
                userGql.firstname,
                userGql.surname,
                userGql.avatar,
                userGql.location.code,
                userGql.friendStatus,
                null
            )
        )
        .toList();
  }

  @Nonnull
  public FriendshipJson friendshipMutation(UserJson targetUser, FriendshipAction action) {
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
  @Step(TestPrefix.GRAPHQL + "Создание входящих запросов дружбы")
  public List<UserJson> createIncomeInvitation(UserJson user, int count) {
    final List<UsersGraphQLClient> result = IntStream.range(0, count)
        .mapToObj(i -> create(RandomDataUtils.getRandomUserName()))
        .toList();
    result.forEach(u -> u.sendInvitation(user));
    return result.stream().map(UsersGraphQLClient::currentUser).toList();
  }

  @Nonnull
  @Step(TestPrefix.GRAPHQL + "Создание исходящих запросов дружбы")
  public List<UserJson> createOutcomeInvitation(int count) {
    final List<UserJson> result = IntStream.range(0, count)
        .mapToObj(i -> create(RandomDataUtils.getRandomUserName()))
        .map(UsersGraphQLClient::currentUser)
        .toList();
    result.forEach(this::sendInvitation);
    return result;
  }

  @Nonnull
  @Step(TestPrefix.GRAPHQL + "Создание друзей")
  public List<UserJson> createFriends(UserJson user, int count) {
    final List<UserJson> result = createIncomeInvitation(user, count);
    result.forEach(this::acceptFriend);
    return result;
  }

  @Nonnull
  private UsersGraphQLClient create(String username) {
    AuthApiClient authClient = new AuthApiClient(null);
    authClient.register(username, UserExtension.DEFAULT_PASSWORD);
    final String token = authClient.login(username, UserExtension.DEFAULT_PASSWORD);
    return new UsersGraphQLClient(token);
  }

  @Nonnull
  private FriendshipJson acceptFriend(UserJson requester) {
    return friendshipMutation(requester, FriendshipAction.ACCEPT);
  }

  @Nonnull
  public FriendshipJson sendInvitation(UserJson requester) {
    return friendshipMutation(requester, FriendshipAction.ADD);
  }

  @Nonnull
  public FriendshipJson declineInvitation(UserJson requester) {
    return friendshipMutation(requester, FriendshipAction.REJECT);
  }
}
