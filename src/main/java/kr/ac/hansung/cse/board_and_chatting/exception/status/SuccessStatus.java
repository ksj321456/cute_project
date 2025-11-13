package kr.ac.hansung.cse.board_and_chatting.exception.status;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus {

    // 회원가입 성공 메세지
    SIGN_UP_SUCCESS(HttpStatus.OK, "SIGN_UP_SUCCESS", "회원가입에 성공했습니다."),

    // 로그인 성공 메세지
    LOG_IN_SUCCESS(HttpStatus.OK, "LOG_IN_SUCCESS", "로그인에 성공했습니다."),

    // 로그아웃 성공 메세지
    LOG_OUT_SUCCESS(HttpStatus.OK, "LOG_OUT_SUCCESS", "로그아웃에 성공했습니다."),

    // 홈페이지 요청 시 세션 검사 성공
    SESSION_TEST_SUCCESS(HttpStatus.OK, "SESSION_TEST_SUCCESS", "세션이 존재합니다."),

    // 홈페이지 요청 시 세션 없음
    NO_SESSION(HttpStatus.UNAUTHORIZED, "NO_SESSION", "세션이 존재하지 않거나 만료됐습니다."),

    // 게시글 등록 성공 메시지
    CREATE_ARTICLE_SUCCESS(HttpStatus.CREATED, "CREATE_ARTICLE_SUCCESS", "게시판 글 작성에 성공했습니다."),

    // 게시글 불러오기 성공
    GET_ARTICLES_SUCCESS(HttpStatus.OK, "GET_ARTICLES_SUCCESS", "게시판 글 불러오기에 성공했습니다."),

    // 한 개의 게시글 불러오기 성공
    GET_ARTICLE_SUCCESS(HttpStatus.OK, "GET_ONE_ARTICLE_SUCCESS", "게시판에서 선택한 글 불러오기에 성공했습니다.")

    ;

    private final HttpStatus status;

    private final String code;

    private final String message;
}
