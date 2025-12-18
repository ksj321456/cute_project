package kr.ac.hansung.cse.board_and_chatting.service.user_service;

import kr.ac.hansung.cse.board_and_chatting.dto.request_dto.UserRequestDto;
import kr.ac.hansung.cse.board_and_chatting.entity.Friend;
import kr.ac.hansung.cse.board_and_chatting.entity.User;
import kr.ac.hansung.cse.board_and_chatting.entity.enums.Authority;
import kr.ac.hansung.cse.board_and_chatting.event.FriendRequestEvent;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.GeneralException;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.LogInException;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.SignUpForException;
import kr.ac.hansung.cse.board_and_chatting.exception.status.ErrorStatus;
import kr.ac.hansung.cse.board_and_chatting.repository.friend_repository.JpaFriendRepository;
import kr.ac.hansung.cse.board_and_chatting.repository.user_repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

@Service
@Primary
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JpaFriendRepository friendRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    // 회원가입 진행 서비스 메소드
    @Transactional
    public User signUpService(UserRequestDto userDto) throws IOException {
        Optional<User> userOptional = userRepository.findByUserId(userDto.getUserId());

        // 이미 존재하는 회원일 경우 예외처리
        if (userOptional.isPresent()) {
            throw new SignUpForException(ErrorStatus.ALREADY_EXISTS_USER);
        }

        byte[] pictureBytes = null;
        if (userDto.getUserPicture() != null && !userDto.getUserPicture().isEmpty()) {
            pictureBytes = userDto.getUserPicture().getBytes();
        }

        if (userDto.getUserId().equals("ADMIN")) {
            User user = User.builder()
                    .nickname(userDto.getNickname())
                    .userId(userDto.getUserId())
                    .password(bCryptPasswordEncoder.encode(userDto.getPassword()))
                    .authority(Authority.ADMIN)
                    .userPicture(pictureBytes)
                    .build();
            userRepository.save(user);
            return user;
        } else {
            User user = User.builder()
                    .nickname(userDto.getNickname())
                    .userId(userDto.getUserId())
                    .password(bCryptPasswordEncoder.encode(userDto.getPassword()))
                    .authority(Authority.USER)
                    .userPicture(pictureBytes)
                    .build();
            userRepository.save(user);
            return user;
        }
    }

    @Transactional
    public User loginService(UserRequestDto.LoginDto userDto) {
        Optional<User> userOptional = userRepository.findByUserId(userDto.getUserId());

        // DB에 해당 User 정보가 없다면 예외처리
        userOptional.orElseThrow(() -> new LogInException(ErrorStatus.NOT_EXISTING_USER));

        userOptional.ifPresent(user -> {
            if (!bCryptPasswordEncoder.matches(userDto.getPassword(), user.getPassword())) {
                throw new LogInException(ErrorStatus.WRONG_PASSWORD);
            }
        });

        // user 객체 반환
        return userOptional.get();
    }

    @Override
    @Transactional
    public void friendRequest(String from, String to) {

        User fromUser = userRepository.findByNickname(from)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_FRIEND_WITH_THE_NICKNAME));

        User toUser = userRepository.findByNickname(to)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_FRIEND_WITH_THE_NICKNAME));

        if (from.equals(to)) throw new GeneralException(ErrorStatus.ENABLE_FRIEND_BY_MYSELF);

        if (friendRepository.existsByRequesterAndReceiver(fromUser, toUser)) throw new GeneralException(ErrorStatus.ALREADY_FRIEND_REQUESTED);

        if (friendRepository.existsByRequesterAndReceiver(fromUser, toUser) && friendRepository.existsByRequesterAndReceiver(toUser, fromUser))
            throw new GeneralException(ErrorStatus.ALREADY_FRIEND);

        Friend friend = Friend.create(fromUser, toUser);

        friendRepository.save(friend);

        applicationEventPublisher.publishEvent(new FriendRequestEvent(friend));
    }
}
