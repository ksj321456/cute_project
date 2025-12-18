package kr.ac.hansung.cse.board_and_chatting.infrastructure;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseNotificationSender {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId) {

        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);

        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        return emitter;
    }

    public void send(Long toId, Map<String, Object> data) {
        SseEmitter emitter = emitters.get(toId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name((String) data.get("type"))
                        .data(data)
                );
            } catch (IOException e) {
                emitters.remove(toId);
            }
        }
    }
}
