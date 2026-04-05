package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.data.UserEntity;
import guru.qa.rangiffler.data.projection.UserWithStatus;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@ParametersAreNonnullByDefault
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

  @Nonnull
  Optional<UserEntity> findByUsername(String username);

  @Nonnull
  @Query("""
      select distinct new guru.qa.rangiffler.data.projection.UserWithStatus(
        u.id, u.username, u.firstname, u.surname, u.country, u.avatar,
        f.status, f.addressee.id as requesterId, f.addressee.id as addresseeId
      )
      from UserEntity u left join FriendshipEntity f
      on (
        (u = f.addressee and f.requester.username = :username)
        or (u = f.requester and f.addressee.username = :username)
      )
      where u.username <> :username
      and (
        f.status = guru.qa.rangiffler.data.FriendshipStatus.PENDING
        or f.status is null
        or (f.status = guru.qa.rangiffler.data.FriendshipStatus.ACCEPTED and f.requester.id = u.id)
      )
      order by u.username asc
      """)
  Page<UserWithStatus> findByUsernameNot(String username, Pageable pageable);

  @Query("""
      select distinct new guru.qa.rangiffler.data.projection.UserWithStatus(
        u.id, u.username, u.firstname, u.surname, u.country, u.avatar,
        f.status, f.addressee.id as requesterId, f.addressee.id as addresseeId
      )
      from UserEntity u left join FriendshipEntity f
      on (
        (u = f.addressee and f.requester.username = :username)
        or (u = f.requester and f.addressee.username = :username)
      )
      where u.username <> :username
      and (
        f.status = guru.qa.rangiffler.data.FriendshipStatus.PENDING
        or f.status is null
        or (f.status = guru.qa.rangiffler.data.FriendshipStatus.ACCEPTED and f.requester.id = u.id)
      )
      and (
        u.username like %:searchQuery%
        or u.firstname like %:searchQuery%
        or u.surname like %:searchQuery%
      )
      order by u.username asc
      """)
  Page<UserWithStatus> findByUsernameNotAndSearchQuery(
      @Param("username") String username,
      @Param("searchQuery") String searchQuery,
      Pageable pageable
  );

  @Nonnull
  @Query("""
      select distinct new guru.qa.rangiffler.data.projection.UserWithStatus(
        u.id, u.username, u.firstname, u.surname, u.country, u.avatar,
        f.status, f.addressee.id as requesterId, f.addressee.id as addresseeId
      )
      from UserEntity u join FriendshipEntity f on u = f.addressee
      where f.status = guru.qa.rangiffler.data.FriendshipStatus.ACCEPTED
      and f.requester = :requester
      order by u.username asc
      """)
  Page<UserWithStatus> findFriends(
      @Param("requester") UserEntity requester,
      Pageable pageable);

  @Nonnull
  @Query("""
      select distinct new guru.qa.rangiffler.data.projection.UserWithStatus(
        u.id, u.username, u.firstname, u.surname, u.country, u.avatar,
        f.status, f.addressee.id as requesterId, f.addressee.id as addresseeId
      )
      from UserEntity u join FriendshipEntity f on u = f.addressee
      where f.status = guru.qa.rangiffler.data.FriendshipStatus.ACCEPTED
      and f.requester = :requester
      and (
        u.username like %:searchQuery%
        or u.firstname like %:searchQuery% or u.surname like %:searchQuery%
      )
      order by u.username asc
      """)
  Page<UserWithStatus> findFriends(
      @Param("requester") UserEntity requester,
      Pageable pageable,
      @Param("searchQuery") String searchQuery);

  @Nonnull
  @Query("""
      select distinct new guru.qa.rangiffler.data.projection.UserWithStatus(
        u.id, u.username, u.firstname, u.surname, u.country, u.avatar,
        f.status, f.addressee.id as requesterId, f.addressee.id as addresseeId
      )
      from UserEntity u join FriendshipEntity f on u = f.addressee
      where f.status = guru.qa.rangiffler.data.FriendshipStatus.ACCEPTED
      and f.requester = :requester
      order by u.username asc
      """)
  List<UserWithStatus> findFriends(@Param("requester") UserEntity requester);

  @Nonnull
  @Query("""
      select distinct new guru.qa.rangiffler.data.projection.UserWithStatus(
        u.id, u.username, u.firstname, u.surname, u.country, u.avatar,
        f.status, f.addressee.id as requesterId, f.addressee.id as addresseeId
      )
      from UserEntity u join FriendshipEntity f on u = f.addressee
      where f.status = guru.qa.rangiffler.data.FriendshipStatus.ACCEPTED
      and f.requester = :requester
      and (
        u.username like %:searchQuery%
        or u.firstname like %:searchQuery%
        or u.surname like %:searchQuery%
      )
      order by u.username asc
      """)
  List<UserWithStatus> findFriends(
      @Param("requester") UserEntity requester,
      @Param("searchQuery") String searchQuery);

  @Nonnull
  @Query("""
      select distinct new guru.qa.rangiffler.data.projection.UserWithStatus(
        u.id, u.username, u.firstname, u.surname, u.country, u.avatar,
        f.status, f.addressee.id as requesterId, f.addressee.id as addresseeId
      )
      from UserEntity u join FriendshipEntity f on u = f.addressee
      where f.status = guru.qa.rangiffler.data.FriendshipStatus.PENDING
      and f.requester = :requester
      order by u.username asc
      """)
  Page<UserWithStatus> findOutcomeInvitations(
      @Param("requester") UserEntity requester,
      Pageable pageable);

  @Nonnull
  @Query("""
      select distinct new guru.qa.rangiffler.data.projection.UserWithStatus(
        u.id, u.username, u.firstname, u.surname, u.country, u.avatar,
        f.status, f.addressee.id as requesterId, f.addressee.id as addresseeId
      )
      from UserEntity u join FriendshipEntity f on u = f.addressee
      where f.status = guru.qa.rangiffler.data.FriendshipStatus.PENDING
      and f.requester = :requester
      and (
        lower(u.username) like lower(concat('%', :searchQuery, '%'))
        or lower(u.surname) like lower(concat('%', :searchQuery, '%'))
      )
      order by u.username asc
      """)
  Page<UserWithStatus> findOutcomeInvitations(
      @Param("requester") UserEntity requester,
      Pageable pageable,
      @Param("searchQuery") String searchQuery);

  @Nonnull
  @Query("""
      select distinct new guru.qa.rangiffler.data.projection.UserWithStatus(
        u.id, u.username, u.firstname, u.surname, u.country, u.avatar,
        f.status, f.addressee.id as requesterId, f.addressee.id as addresseeId
      )
      from UserEntity u join FriendshipEntity f on u = f.addressee
      where f.status = guru.qa.rangiffler.data.FriendshipStatus.PENDING
      and f.requester = :requester
      order by u.username asc
      """)
  List<UserWithStatus> findOutcomeInvitations(@Param("requester") UserEntity requester);

  @Nonnull
  @Query("""
      select distinct new guru.qa.rangiffler.data.projection.UserWithStatus(
        u.id, u.username, u.firstname, u.surname, u.country, u.avatar,
        f.status, f.addressee.id as requesterId, f.addressee.id as addresseeId
      )
      from UserEntity u join FriendshipEntity f on u = f.addressee
      where f.status = guru.qa.rangiffler.data.FriendshipStatus.PENDING
      and f.requester = :requester
      and (
        lower(u.username) like lower(concat('%', :searchQuery, '%'))
        or lower(u.surname) like lower(concat('%', :searchQuery, '%'))
      )
      order by u.username asc
      """)
  List<UserWithStatus> findOutcomeInvitations(
      @Param("requester") UserEntity requester,
      @Param("searchQuery") String searchQuery);

  @Nonnull
  @Query("""
      select distinct new guru.qa.rangiffler.data.projection.UserWithStatus(
        u.id, u.username, u.firstname, u.surname, u.country, u.avatar,
        f.status, f.addressee.id as requesterId, f.addressee.id as addresseeId
      )
      from UserEntity u join FriendshipEntity f on u = f.requester
      where f.status = guru.qa.rangiffler.data.FriendshipStatus.PENDING
      and f.addressee = :addressee
      order by u.username asc
      """)
  Page<UserWithStatus> findIncomeInvitations(
      @Param("addressee") UserEntity requester,
      Pageable pageable);

  @Nonnull
  @Query("""
      select distinct new guru.qa.rangiffler.data.projection.UserWithStatus(
        u.id, u.username, u.firstname, u.surname, u.country, u.avatar,
        f.status, f.addressee.id as requesterId, f.addressee.id as addresseeId
      )
      from UserEntity u join FriendshipEntity f on u = f.requester
      where f.status = guru.qa.rangiffler.data.FriendshipStatus.PENDING
      and f.addressee = :addressee
      and (
        lower(u.username) like lower(concat('%', :searchQuery, '%'))
        or lower(u.surname) like lower(concat('%', :searchQuery, '%'))
      )
      order by u.username asc
      """)
  Page<UserWithStatus> findIncomeInvitations(
      @Param("addressee") UserEntity requester,
      Pageable pageable,
      @Param("searchQuery") String searchQuery);
}
