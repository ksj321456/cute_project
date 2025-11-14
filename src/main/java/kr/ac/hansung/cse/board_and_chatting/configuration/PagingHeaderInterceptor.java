package kr.ac.hansung.cse.board_and_chatting.configuration;

import kr.ac.hansung.cse.board_and_chatting.dto.request_header_dto.RequestHeaderDto;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.InadequatePagingHeaderFormat;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.NoPagingParameterException;
import kr.ac.hansung.cse.board_and_chatting.exception.status.ErrorStatus;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerInterceptor;

public class PagingHeaderInterceptor implements HandlerMethodArgumentResolver {

    // 컨트롤러 메소드에서 요구하는 파라미터 타입이 RequestHeaderDto.PagingHeader일 경우에만 해당 Resolver 실행
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(RequestHeaderDto.PagingHeader.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        String pageHeader = webRequest.getParameter("page");
        String sizeHeader = webRequest.getParameter("size");

        // 하나라도 없으면 예외 발생
        if (pageHeader == null || sizeHeader == null) {
            throw new NoPagingParameterException(ErrorStatus.NO_PAGING_HEADER);
        }

        int page, size;

        try {
            page = Integer.parseInt(pageHeader);
            size = Integer.parseInt(sizeHeader);

        } catch (NumberFormatException e) {
            throw new InadequatePagingHeaderFormat(ErrorStatus.INADEQUATE_PAGING_HEADER_FORMAT);
        }

        RequestHeaderDto.PagingHeader dto = RequestHeaderDto.PagingHeader.builder()
                .page(page)
                .size(size)
                .build();

        return dto;
    }
}
