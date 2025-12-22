package kr.ac.hansung.cse.board_and_chatting.event;

import kr.ac.hansung.cse.board_and_chatting.entity.Notification;
import kr.ac.hansung.cse.board_and_chatting.infrastructure.SseNotificationSender;
import kr.ac.hansung.cse.board_and_chatting.repository.friend_repository.JpaFriendRepository;
import kr.ac.hansung.cse.board_and_chatting.repository.notification_repository.JpaNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class FriendRequestEventListener {

    private final JpaNotificationRepository notificationRepository;
    private final SseNotificationSender sseNotificationSender;

    @Async("notificationTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW) // 새로운 트랜잭션 생성
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFriendRequestEvent(FriendRequestEvent friendRequestEvent) {
        Notification notification = new Notification();
        notification.setType("friend-request");
        notification.setUser(friendRequestEvent.getFriend().getReceiver());
        notification.setContent(friendRequestEvent.getFriend().getRequester().getNickname() + "님이 친구 요청을 보냈습니다.");
        notificationRepository.save(notification);

        sseNotificationSender.send(notification.getUser().getId(), Map.of(
                "type", "friend-request",
                "from", friendRequestEvent.getFriend().getRequester().getNickname(),
                "content", notification.getContent(),
                "isRead", notification.isRead()
        ));
    }
}
