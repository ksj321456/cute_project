package kr.ac.hansung.cse.board_and_chatting.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.ac.hansung.cse.board_and_chatting.dto.request_dto.NotificationRequestDto;
import kr.ac.hansung.cse.board_and_chatting.entity.Notification;
import kr.ac.hansung.cse.board_and_chatting.entity.User;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.AuthenticationException;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.GeneralException;
import kr.ac.hansung.cse.board_and_chatting.exception.status.ErrorStatus;
import kr.ac.hansung.cse.board_and_chatting.infrastructure.SseNotificationSender;
import kr.ac.hansung.cse.board_and_chatting.service.authentication_service.SessionService;
import kr.ac.hansung.cse.board_and_chatting.service.friend_service.FriendService;
import kr.ac.hansung.cse.board_and_chatting.service.notification_service.NotificationService;
import kr.ac.hansung.cse.board_and_chatting.service.user_service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
    private final FriendService friendService;

    // SSE 연결 수립 -> 밀린 Notification들 받기
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
        List<Notification> unReadNotifications = notificationService.findByUser(user);

        unReadNotifications.forEach(notification -> {
            sseNotificationSender.send(userId, Map.of(
                    "type", notification.getType(),
                    "content", notification.getContent(),
                    "isRead", notification.isRead()
            ));

            // 해당 UPDATE 작업은 비동기 스레드로 하자~!
            if (!notification.isRead()) {
                notification.setRead(true);
                notificationService.save(notification);
            }
        });
        return sseEmitter;
    }

    @PostMapping("/accept_friend_request")
    public ResponseEntity<?> acceptFriendRequest(@Valid @RequestBody NotificationRequestDto.AcceptFriendRequestDto acceptFriendRequestDto,
                                                 BindingResult bindingResult,
                                                 HttpServletRequest request) {

        if (sessionService.getSession(request) == null) throw new AuthenticationException(ErrorStatus.NO_AUTHENTICATION);

        if (bindingResult.hasErrors()) throw new GeneralException(ErrorStatus.INTERNAL_BAD_REQUEST);

        User receiver = (User) sessionService.getSession(request).getAttribute("user");

        String requesterNickname = acceptFriendRequestDto.getRequesterNickname();

        // Friend 테이블에 status 컬럼을 FRIEND로 변경
        User requester = friendService.changeStatus(receiver, requesterNickname);

        // Notification 테이블에 친구 수락 알림 저장
        // 첫번째 매개변수 requester는 처음에 친구 요청을 보낸 유저, 두번째 매개변수 receiver는 친구 요청을 수락한 User
        Notification notification = notificationService.acceptFriendRequest(requester, receiver);

        // 친구 요청 수락 알림받을 Emitter가 연결되어 있으면 전송(isRead = true로 UPDATE),
        // 그렇지 않으면 무시(어차피, SSE 연결 수립 시, 알림을 받기 때문)
        if (sseNotificationSender.isEmitterLogin(requester.getId())) {
            sseNotificationSender.send(requester.getId(), Map.of(
                    "type", notification.getType(),
                    "content", notification.getContent(),
                    "isRead", notification.isRead()
            ));

            // 해당 UPDATE 작업은 비동기 스레드로 하자~!
            if (!notification.isRead()) {
                notification.setRead(true);
                notificationService.save(notification);
            }
        }

        // 기존에 Friend 테이블에 있던 요청 데이터는 삭제
        friendService.deleteByRequesterAndReceiver(requester, receiver);

        return ResponseEntity.ok("친구 수락 요청 성공");
    }
}
