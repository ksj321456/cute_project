package kr.ac.hansung.cse.board_and_chatting.configuration.resolver;

import kr.ac.hansung.cse.board_and_chatting.dto.request_parameter_dto.RequestParameterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestParser {

    private final List<RequestParameterState> states;

    public Object parse(MethodParameter parameter, NativeWebRequest request) {

        return states.stream()
                .filter(s -> s.supports(parameter))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("지원되지 않는 DTO"))
                .parse(request);
    }
}
