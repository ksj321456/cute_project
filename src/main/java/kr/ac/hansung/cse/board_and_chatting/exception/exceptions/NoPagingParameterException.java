package kr.ac.hansung.cse.board_and_chatting.exception.exceptions;

import kr.ac.hansung.cse.board_and_chatting.exception.status.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Getter
public class NoPagingParameterException extends GeneralException {

    public NoPagingParameterException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
