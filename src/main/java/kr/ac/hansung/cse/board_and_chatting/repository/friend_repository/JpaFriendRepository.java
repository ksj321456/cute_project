package kr.ac.hansung.cse.board_and_chatting.repository.friend_repository;

import kr.ac.hansung.cse.board_and_chatting.entity.Friend;
import kr.ac.hansung.cse.board_and_chatting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaFriendRepository extends JpaRepository<Friend, Long> {

    boolean existsByRequesterAndReceiver(User requester, User receiver);
}
