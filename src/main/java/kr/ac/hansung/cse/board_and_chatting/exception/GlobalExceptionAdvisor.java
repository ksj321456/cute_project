package kr.ac.hansung.cse.board_and_chatting.exception;

import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.AuthenticationException;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.GeneralException;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.NoPagingParameterException;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.ValidationException;
import kr.ac.hansung.cse.board_and_chatting.exception.status.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Map;

@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionAdvisor<T> {

    @ExceptionHandler(GeneralException.class)
    public <T> ResponseEntity<ErrorResponse<T>> handleGeneralException(GeneralException e) {
        log.error(String.valueOf(e.getStatus().getStatus()));
        log.error(e.getStatus().getCode());
        log.error(e.getStatus().getMessage());

        // result가 없는 경우
        return ErrorResponse.<T>toResponseEntity(e.getStatus());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse<List<Map<String, String>>>> handleValidationException(ValidationException e) {

        List<Map<String, String>> errorList = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> Map.of(
                        "field", fieldError.getField(),
                        "reason", fieldError.getDefaultMessage()
                ))
                .toList();

        log.error(String.valueOf(e.getErrorStatus().getStatus()));
        log.error(e.getErrorStatus().getCode());
        log.error(e.getErrorStatus().getMessage());

        return ErrorResponse.toResponseEntity(e.getErrorStatus(), errorList);
    }

    @ExceptionHandler(AuthenticationException.class)
    public <T> ResponseEntity<ErrorResponse<T>> handleDataIntegrityViolationException(AuthenticationException e) {
        log.error(String.valueOf(e.getErrorStatus().getStatus()));
        log.error(e.getErrorStatus().getCode());
        log.error(e.getErrorStatus().getMessage());
        return ErrorResponse.toResponseEntity(e.getErrorStatus());
    }
}
