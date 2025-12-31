package kr.ac.hansung.cse.board_and_chatting.configuration.resolver;

import kr.ac.hansung.cse.board_and_chatting.dto.request_parameter_dto.RequestParameterDto;
import kr.ac.hansung.cse.board_and_chatting.dto.request_parameter_dto.RequestParameterDtoImpl;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.GeneralException;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.InadequatePagingHeaderFormat;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.NoPagingParameterException;
import kr.ac.hansung.cse.board_and_chatting.exception.status.ErrorStatus;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

@Component
public class ViewMyProfilePagingState implements RequestParameterState {
    @Override
    public boolean supports(MethodParameter parameter) {
        return parameter.getParameterType().equals(RequestParameterDtoImpl.ViewMyProfileParameterDto.class);
    }

    @Override
    public RequestParameterDto parse(NativeWebRequest request) {
        String postPageStr = request.getParameter("post_page");
        String postSizeStr = request.getParameter("post_size");

        String commentPageStr = request.getParameter("comment_page");
        String commentSizeStr = request.getParameter("comment_size");

        String friendPageStr = request.getParameter("friend_page");
        String friendSizeStr = request.getParameter("friend_size");

        if (postPageStr == null && postSizeStr == null && commentPageStr == null && commentSizeStr == null && friendPageStr == null && friendSizeStr == null) {
            throw new NoPagingParameterException(ErrorStatus.NO_PAGING_HEADER);
        }

        int postPage;
        int postSize;

        int commentPage;
        int commentSize;

        int friendPage;
        int friendSize;

        try {
            postPage = Integer.parseInt(postPageStr);
            postSize = Integer.parseInt(postSizeStr);

            commentPage = Integer.parseInt(commentPageStr);
            commentSize = Integer.parseInt(commentSizeStr);

            friendPage = Integer.parseInt(friendPageStr);
            friendSize = Integer.parseInt(friendSizeStr);
        } catch (NumberFormatException e) {
            throw new InadequatePagingHeaderFormat(ErrorStatus.INADEQUATE_PAGING_HEADER_FORMAT);
        }

        RequestParameterDtoImpl.ViewMyProfileParameterDto dto = RequestParameterDtoImpl.ViewMyProfileParameterDto.builder()
                .postPage(postPage)
                .postSize(postSize)
                .commentPage(commentPage)
                .commentSize(commentSize)
                .friendPage(friendPage)
                .friendSize(friendSize)
                .build();
        return dto;
    }
}
