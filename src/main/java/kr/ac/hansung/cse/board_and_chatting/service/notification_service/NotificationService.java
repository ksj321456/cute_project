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
}
