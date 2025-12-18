package kr.ac.hansung.cse.board_and_chatting.repository.user_repository;

import kr.ac.hansung.cse.board_and_chatting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String userId);

    Optional<User> findByNickname(String nickname);
}
