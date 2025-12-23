package kr.ac.hansung.cse.board_and_chatting.service.notification_service;

import kr.ac.hansung.cse.board_and_chatting.entity.Notification;
import kr.ac.hansung.cse.board_and_chatting.entity.User;
import kr.ac.hansung.cse.board_and_chatting.repository.notification_repository.JpaNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JpaNotificationRepository notificationRepository;

    @Transactional
    public List<Notification> findByUserAndIsReadFalse(User user) {
        return notificationRepository.findByUserAndIsReadFalse(user);
    }

    @Transactional
    public List<Notification> findByUser(User user) {
        return notificationRepository.findByUser(user);
    }

    @Transactional
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    // 매개변수 receiver는 해당 알림을 받을 유저 즉, 처음에 친구 요청을 보낸 유저이다.
    // 매개변수 accepter는 친구 요청을 수락한 유저이다.
    @Transactional
    public Notification acceptFriendRequest(User receiver, User accepter) {
        Notification notification = new Notification();
        notification.setType("friend-accept");
        notification.setUser(receiver);
        notification.setContent(accepter.getNickname() + "님이 친구 요청을 수락했습니다.");
        save(notification);
        return notification;
    }
}
