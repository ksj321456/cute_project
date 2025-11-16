package kr.ac.hansung.cse.board_and_chatting.configuration.resolver;

import kr.ac.hansung.cse.board_and_chatting.dto.request_parameter_dto.RequestHeaderDto;
import kr.ac.hansung.cse.board_and_chatting.dto.request_parameter_dto.RequestParameterDto;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.InadequatePagingHeaderFormat;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.NoPagingParameterException;
import kr.ac.hansung.cse.board_and_chatting.exception.status.ErrorStatus;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

@Component
public class PagingParameterState implements RequestParameterState {

    @Override
    public boolean supports(MethodParameter parameter) {
        return parameter.getParameterType().equals(RequestHeaderDto.PagingHeader.class);
    }

    @Override
    public RequestParameterDto parse(NativeWebRequest request) {
        String pageHeader = request.getParameter("page");
        String sizeHeader = request.getParameter("size");

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
