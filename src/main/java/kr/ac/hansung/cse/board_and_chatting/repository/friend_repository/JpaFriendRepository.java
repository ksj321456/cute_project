package kr.ac.hansung.cse.board_and_chatting.repository.friend_repository;

import kr.ac.hansung.cse.board_and_chatting.entity.Friend;
import kr.ac.hansung.cse.board_and_chatting.entity.User;
import kr.ac.hansung.cse.board_and_chatting.entity.enums.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaFriendRepository extends JpaRepository<Friend, Long> {

    boolean existsByRequesterAndReceiver(User requester, User receiver);

    Optional<Friend> findByRequesterAndReceiver(User requester, User receiver);

    void deleteByRequesterAndReceiverAndStatus(User requester, User receiver, FriendStatus status);}
