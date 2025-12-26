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

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final JpaFriendRepository friendRepository;
    private final UserRepository userRepository;

    // Receiver가 승낙했을 때, Friend 테이블 status를 FRIEND로 변경하기
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

        int updateCnt = friendRepository.updateStatusToFriend(FriendStatus.FRIEND, LocalDateTime.now(), receiver, requester, FriendStatus.REQUESTED);

        if (updateCnt == 0) throw new GeneralException(ErrorStatus.NO_FRIEND_RECORD);

        return requester;
    }

    @Transactional
    public void deleteByRequesterAndReceiver(User requester, User receiver) {
        friendRepository.deleteByRequesterAndReceiverAndStatus(requester, receiver, FriendStatus.REQUESTED);
    }

    // Receiver가 거절했을 때, Friend 테이블 해당 row 삭제 -> Requester User 객체 반환
    @Transactional
    public User deleteByRequesterAndUserAndReturnRequester(User receiver, String requesterNickname) {
        Optional<User> userOptional = userRepository.findByNickname(requesterNickname);
        User requester = userOptional.orElseThrow(() -> new GeneralException(ErrorStatus.NOT_EXISTING_USER));

        friendRepository.deleteByRequesterAndReceiverAndStatus(requester, receiver, FriendStatus.REQUESTED);
        return requester;
    }
}
