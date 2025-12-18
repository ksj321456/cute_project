package kr.ac.hansung.cse.board_and_chatting.repository.user_repository;

import kr.ac.hansung.cse.board_and_chatting.entity.User;

import java.util.Optional;


public interface UserRepository {
    Optional<User> findByUserId(String userId);
    User save(User user);
    Optional<User> findByNickname(String nickname);
}
