package kr.ac.hansung.cse.board_and_chatting.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// HTTP body에 담을 객체
// NULL인 JSON field는 생략, NULL이 아닌 field와 값들만 응답 JSON으로 전송
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
@AllArgsConstructor
public class APIResponse<T> {

    private final HttpStatus status;

    private String code;

    private String message;

    private T result;


    // 클라이언트에 넘겨줄 ResponseEntity 객체 생성 메소드
    public static <T> ResponseEntity<APIResponse<T>> toResponseEntity(APIResponse<T> apiResponse) {
        return ResponseEntity.status(apiResponse.getStatus())
                .body(apiResponse);
    }
}
