package kr.ac.hansung.cse.board_and_chatting.controller.specification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.ac.hansung.cse.board_and_chatting.dto.request_dto.BoardRequestDto;
import kr.ac.hansung.cse.board_and_chatting.dto.request_header_dto.RequestHeaderDto;
import kr.ac.hansung.cse.board_and_chatting.dto.response_dto.BoardResponseDto;
import kr.ac.hansung.cse.board_and_chatting.exception.APIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Tag(name = "게시판 URI", description = "게시판 관련 작업 API 목록입니다.")
public interface BoardSpeicification {

    @Operation(summary = "게시글을 페이징을 통해서 보기", description = "Request Header를 통해 페이징 파라미터를 넘기고 해당 페이징 파라미터를 통해 게시글 목록을 조회합니다.",
                parameters = {
                        @Parameter(
                                name = "page",
                                description = "페이지 번호(0부터 시작)",
                                in = ParameterIn.QUERY,
                                required = true,
                                example = "0"
                        ),
                        @Parameter(
                                name = "size",
                                description = "각 페이지의 요소 크기",
                                in = ParameterIn.QUERY,
                                required = true,
                                example = "20"
                        )
                }
    )
    @GetMapping("/get_articles")
    public ResponseEntity<APIResponse<BoardResponseDto.GeneralArticlesResponseDto>> getArticles(
            @Valid RequestHeaderDto.PagingHeader pagingHeader,
            HttpServletRequest request
    );
}
