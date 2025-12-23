package kr.ac.hansung.cse.board_and_chatting.service.friend_service;

import kr.ac.hansung.cse.board_and_chatting.entity.Friend;
import kr.ac.hansung.cse.board_and_chatting.entity.User;
import kr.ac.hansung.cse.board_and_chatting.entity.enums.FriendStatus;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.GeneralException;
import kr.ac.hansung.cse.board_and_chatting.exception.status.ErrorStatus;
import kr.ac.hansung.cse.board_and_chatting.repository.friend_repository.JpaFriendRepository;
import kr.ac.hansung.cse.board_and_chatting.repository.user_repository.UserRepository;
import kr.ac.hansung.cse.board_and_chatting.service.user_service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final JpaFriendRepository friendRepository;
    private final UserRepository userRepository;

    // Receiver가 승락했을 때, Friend 테이블 status를 FRIEND로 변경하기
    // 매개변수인 receiver와 requesterNickname으로 Friend 테이블 검사 후, status를 FRIEND로 변경
    // 반환값은 친구 요청을 보낸 유저(Notification 객체 생성을 위해)
    // 동기로 쿼리 3번 ㅋㅋ
    @Transactional
    public User changeStatus(User receiver, String requesterNickname) {

        // requesterNickname에 해당하는 User 객체 얻기
        System.out.println("requesterNickname: " + requesterNickname);
        Optional<User> userOptional = userRepository.findByNickname(requesterNickname);

        User requester = userOptional.orElseThrow(() -> new GeneralException(ErrorStatus.NOT_EXISTING_USER));

        if (!friendRepository.existsByRequesterAndReceiver(requester, receiver)) throw new GeneralException(ErrorStatus.NO_FRIEND_RECORD);

        // requester, receiver에 대응하는 Friend 객체 얻기
        Optional<Friend> friendOptional = friendRepository.findByRequesterAndReceiver(requester, receiver);

        Friend friend = friendOptional.orElseThrow(() -> new GeneralException(ErrorStatus.NO_FRIEND_RECORD));

        // 상태 갱신
        friend.setStatus(FriendStatus.FRIEND);

        // UPDATE 쿼리
        friendRepository.save(friend);

        return requester;
    }

    @Transactional
    public void deleteByRequesterAndReceiver(User requester, User receiver) {
        friendRepository.deleteByRequesterAndReceiverAndStatus(requester, receiver, FriendStatus.REQUESTED);
    }
}
