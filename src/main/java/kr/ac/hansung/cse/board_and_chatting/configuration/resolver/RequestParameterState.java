package kr.ac.hansung.cse.board_and_chatting.configuration.resolver;

import kr.ac.hansung.cse.board_and_chatting.dto.request_parameter_dto.RequestParameterDto;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

public interface RequestParameterState {

    // 어떤 State에 대응할것인가?
    boolean supports(MethodParameter parameter);

    // 해당 State에서의 작업
    RequestParameterDto parse(NativeWebRequest request);
}
