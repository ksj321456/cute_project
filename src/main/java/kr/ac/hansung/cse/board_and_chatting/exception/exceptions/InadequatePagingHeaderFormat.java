package kr.ac.hansung.cse.board_and_chatting.exception.exceptions;

import kr.ac.hansung.cse.board_and_chatting.exception.status.ErrorStatus;
import lombok.Getter;

@Getter
public class InadequatePagingHeaderFormat extends GeneralException{

    public InadequatePagingHeaderFormat(ErrorStatus status) {
        super(status);
    }
}
