package kr.ac.hansung.cse.board_and_chatting.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.ac.hansung.cse.board_and_chatting.controller.specification.BoardSpeicification;
import kr.ac.hansung.cse.board_and_chatting.dto.request_dto.BoardRequestDto;
import kr.ac.hansung.cse.board_and_chatting.dto.request_header_dto.RequestHeaderDto;
import kr.ac.hansung.cse.board_and_chatting.dto.response_dto.BoardResponseDto;
import kr.ac.hansung.cse.board_and_chatting.entity.Board;
import kr.ac.hansung.cse.board_and_chatting.entity.User;
import kr.ac.hansung.cse.board_and_chatting.entity.enums.Authority;
import kr.ac.hansung.cse.board_and_chatting.exception.APIResponse;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.AuthenticationException;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.ValidationException;
import kr.ac.hansung.cse.board_and_chatting.exception.status.ErrorStatus;
import kr.ac.hansung.cse.board_and_chatting.exception.status.SuccessStatus;
import kr.ac.hansung.cse.board_and_chatting.service.board_service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.TreeSet;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class BoardController implements BoardSpeicification {

    private final BoardService boardService;

    // 게시글 작성 로직
    @PostMapping("/create-article")
    public ResponseEntity<?> createArticle(@Valid @RequestBody BoardRequestDto.CreateArticleRequest createArticleRequest,
                                           BindingResult bindingResult,
                                                     HttpServletRequest request
                                                     ) {
        // Validation 검증
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult, ErrorStatus.NOT_SUFFICIENT_DATA_FOR_CREATING_ARITLCE);
        }


        HttpSession session = request.getSession();
        // session이 없으면 죽어.
        if (session.getAttribute("user") == null) {
            throw new AuthenticationException(ErrorStatus.NO_AUTHENTICATION);
        }

        User user = (User) session.getAttribute("user");

        boardService.saveArticle(createArticleRequest, user);
        return APIResponse.toResponseEntity(
                APIResponse.builder()
                        .status(SuccessStatus.CREATE_ARTICLE_SUCCESS.getStatus())
                        .code(SuccessStatus.CREATE_ARTICLE_SUCCESS.getCode())
                        .message(SuccessStatus.CREATE_ARTICLE_SUCCESS.getMessage())
                        .build()
        );
    }

    // 게시글 하나 보기
    @GetMapping("/get-article/{id}")
    public ResponseEntity<?> getArticle(@PathVariable(value = "id") Long id,
                                        @RequestParam("page") int commentPage,
                                        @RequestParam("size") int commentSize,
                                        HttpServletRequest request
                                        ) {
        HttpSession session = request.getSession();
        // 인증/인가 작업 예외 처리
        if (session.getAttribute("user") == null) {
            throw new AuthenticationException(ErrorStatus.NO_AUTHENTICATION);
        }

        User user = (User) session.getAttribute("user");
        BoardResponseDto.OneArticleResponseDto oneArticleResponseDto = boardService.getOneArticle(id, user, commentPage, commentSize);
        APIResponse apiResponse = APIResponse.builder()
                .status(SuccessStatus.GET_ARTICLE_SUCCESS.getStatus())
                .code(SuccessStatus.GET_ARTICLE_SUCCESS.getCode())
                .message(SuccessStatus.GET_ARTICLE_SUCCESS.getMessage())
                .result(oneArticleResponseDto)
                .build();

        return APIResponse.toResponseEntity(apiResponse);
    }

    // 게시글 전체 불러오기
    public ResponseEntity<APIResponse<BoardResponseDto.GeneralArticlesResponseDto>> getArticles(
            @Valid RequestHeaderDto.PagingHeader pagingHeader,
            HttpServletRequest request
    ) {

        HttpSession session = request.getSession();
        // 인증/인가 작업 예외 처리
        if (session.getAttribute("user") == null) {
            throw new AuthenticationException(ErrorStatus.NO_AUTHENTICATION);
        }

        int page = pagingHeader.getPage();
        int size = pagingHeader.getSize();

        // 응답 시 넘겨줄 객체 생성
        BoardResponseDto.GeneralArticlesResponseDto generalArticlesResponseDto = boardService.getArticle(page, size);

        APIResponse apiResponse = APIResponse.builder()
                .status(SuccessStatus.GET_ARTICLES_SUCCESS.getStatus())
                .code(SuccessStatus.GET_ARTICLES_SUCCESS.getCode())
                .message(SuccessStatus.GET_ARTICLES_SUCCESS.getMessage())
                .result(generalArticlesResponseDto)
                .build();

        return APIResponse.toResponseEntity(apiResponse);
    }

    // 게시글 제목 검색 기능
    @GetMapping("/get-articles-with-title")
    public ResponseEntity<?> getArticleWithTitle(
            // 파라미터로 page, size, title받음.
            @Valid @ModelAttribute BoardRequestDto.GetArticleWithTitleRequestParameters getArticleWithTitleRequestParameters,
            BindingResult bindingResult,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession();
        // 인증/인가 작업 예외 처리
        if (session.getAttribute("user") == null) {
            throw new AuthenticationException(ErrorStatus.NO_AUTHENTICATION);
        }

        // 유효성 검사 예외 처리
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult, ErrorStatus.MISSING_REQUIRED_PARAMETERS);
        }

        String title = getArticleWithTitleRequestParameters.getTitle();
        int page = getArticleWithTitleRequestParameters.getPage();
        int size = getArticleWithTitleRequestParameters.getSize();

        // 응답 시 넘겨줄 객체 생성
        BoardResponseDto.GeneralArticlesResponseDto generalArticlesResponseDto = boardService.getArticlesWithTitle(title, page, size);

        APIResponse apiResponse = APIResponse.builder()
                .status(SuccessStatus.GET_ARTICLES_SUCCESS.getStatus())
                .code(SuccessStatus.GET_ARTICLES_SUCCESS.getCode())
                .message(SuccessStatus.GET_ARTICLES_SUCCESS.getMessage())
                .result(generalArticlesResponseDto)
                .build();

        return APIResponse.toResponseEntity(apiResponse);
    }

    // 게시글 제목 OR 내용 검색 기능
    @GetMapping("/get-articles-with-title-and-content")
    public <T> ResponseEntity<APIResponse<T>> getArticleWithTitleAndContent(
            @Valid @ModelAttribute BoardRequestDto.GetArticleWithTitleOrContentRequestParameters getArticleWithTitleAndContentRequestParameters,
            BindingResult bindingResult,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession();
        // 인증/인가 작업 예외 처리
        if (session.getAttribute("user") == null) {
            throw new AuthenticationException(ErrorStatus.NO_AUTHENTICATION);
        }

        // 유효성 검사 예외 처리
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult, ErrorStatus.MISSING_REQUIRED_PARAMETERS);
        }

        String title = getArticleWithTitleAndContentRequestParameters.getTitle();
        String content = getArticleWithTitleAndContentRequestParameters.getContent();
        int page = getArticleWithTitleAndContentRequestParameters.getPage();
        int size = getArticleWithTitleAndContentRequestParameters.getSize();

        BoardResponseDto.GeneralArticlesResponseDto generalArticlesResponseDto = boardService.getArticleWithTitleOrContent(title, content, page, size);

        APIResponse apiResponse = APIResponse.builder()
                .status(SuccessStatus.GET_ARTICLES_SUCCESS.getStatus())
                .code(SuccessStatus.GET_ARTICLES_SUCCESS.getCode())
                .message(SuccessStatus.GET_ARTICLES_SUCCESS.getMessage())
                .result(generalArticlesResponseDto)
                .build();

        return APIResponse.toResponseEntity(apiResponse);
    }
}
