package kr.ac.hansung.cse.board_and_chatting.repository.friend_repository;

import kr.ac.hansung.cse.board_and_chatting.entity.Friend;
import kr.ac.hansung.cse.board_and_chatting.entity.User;
import kr.ac.hansung.cse.board_and_chatting.entity.enums.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface JpaFriendRepository extends JpaRepository<Friend, Long> {

    boolean existsByRequesterAndReceiver(User requester, User receiver);

    void deleteByRequesterAndReceiverAndStatus(User requester, User receiver, FriendStatus status);

    @Modifying
    @Query("UPDATE Friend f SET f.status = :newStatus, f.updatedAt = :updatedAt " +
            "WHERE f.receiver = :receiver AND f.requester = :requester AND f.status = :currentStatus")
    int updateStatusToFriend(
            @Param("newStatus") FriendStatus friendStatus,
            @Param("updatedAt")LocalDateTime updatedAt,
            @Param("receiver") User receiver,
            @Param("requester") User requester,
            @Param("currentStatus") FriendStatus currentStatus
            );
}
