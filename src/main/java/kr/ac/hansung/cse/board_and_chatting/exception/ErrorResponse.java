package kr.ac.hansung.cse.board_and_chatting.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.ac.hansung.cse.board_and_chatting.exception.status.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
@Builder
// HTTP body에 담을 객체
// NULL인 JSON field는 생략, NULL이 아닌 field와 값들만 응답 JSON으로 전송
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse<T> {

    private final HttpStatus status;

    private String code;

    private String message;

    private T result;

    // 제네릭 적용: result가 없는 경우
    public static <T> ResponseEntity<ErrorResponse<T>> toResponseEntity(ErrorStatus status) {
        return ResponseEntity
                .status(status.getStatus())
                .body(ErrorResponse.<T>builder()
                        .status(status.getStatus())
                        .code(status.getCode())
                        .message(status.getMessage())
                        .build()
                );
    }

    // 제네릭 적용: result가 있는 경우
    public static <T> ResponseEntity<ErrorResponse<T>> toResponseEntity(ErrorStatus status, T result) {
        return ResponseEntity
                .status(status.getStatus())
                .body(ErrorResponse.<T>builder()
                        .status(status.getStatus())
                        .code(status.getCode())
                        .message(status.getMessage())
                        .result(result)
                        .build());
    }
}
