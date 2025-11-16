package kr.ac.hansung.cse.board_and_chatting.configuration.resolver;

import kr.ac.hansung.cse.board_and_chatting.dto.request_parameter_dto.RequestParameterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class RequestParameterResolver implements HandlerMethodArgumentResolver {

    private final RequestParser requestParser;

    // 컨트롤러 메소드에서 요구하는 파라미터 타입이 RequestHeaderDto.PagingHeader일 경우에만 해당 Resolver 실행
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(RequestParameterDto.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        return requestParser.parse(parameter, webRequest);
    }
}
