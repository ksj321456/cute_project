package kr.ac.hansung.cse.board_and_chatting.repository.user_repository;

import kr.ac.hansung.cse.board_and_chatting.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
//@Profile("jpa")
public class UserJpaRepository implements UserRepository {

    private final JpaUserRepository userRepository;

    @Autowired
    public UserJpaRepository(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public Optional<User> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }
}
