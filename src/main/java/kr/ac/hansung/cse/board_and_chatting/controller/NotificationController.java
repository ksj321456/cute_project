package kr.ac.hansung.cse.board_and_chatting.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.ac.hansung.cse.board_and_chatting.entity.Notification;
import kr.ac.hansung.cse.board_and_chatting.entity.User;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.AuthenticationException;
import kr.ac.hansung.cse.board_and_chatting.exception.status.ErrorStatus;
import kr.ac.hansung.cse.board_and_chatting.infrastructure.SseNotificationSender;
import kr.ac.hansung.cse.board_and_chatting.service.authentication_service.SessionService;
import kr.ac.hansung.cse.board_and_chatting.service.notification_service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {

    private final SseNotificationSender sseNotificationSender;
    private final SessionService sessionService;
    private final NotificationService notificationService;

    // SSE 연결 수립
    @GetMapping(value = "/notifications/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(HttpServletRequest request) {

        if (sessionService.getSession(request) == null) throw new AuthenticationException(ErrorStatus.NO_AUTHENTICATION);

        User user = (User) sessionService.getSession(request).getAttribute("user");
        Long userId = user.getId();

        SseEmitter sseEmitter = sseNotificationSender.subscribe(userId);

        // 1. 일단 연결 성공 메시지를 즉시 보냄 (이때는 DB 연결 X)
        try {
            sseEmitter.send(SseEmitter.event().name("connect").data("connected"));
        } catch (IOException e) { /* ... */ }

        // 밀린 알림들 가져오기
        List<Notification> unReadNotifications = notificationService.findByUserAndIsReadFalse(user);
        System.out.println("알림 수: " + unReadNotifications.size());

        unReadNotifications.forEach(notification -> {
            sseNotificationSender.send(userId, Map.of(
                    "type", notification.getType(),
                    "content", notification.getContent()
            ));
        });
        return sseEmitter;
    }
}
