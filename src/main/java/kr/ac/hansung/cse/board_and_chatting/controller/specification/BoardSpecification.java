package kr.ac.hansung.cse.board_and_chatting.controller.specification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.ac.hansung.cse.board_and_chatting.dto.request_parameter_dto.RequestHeaderDto;
import kr.ac.hansung.cse.board_and_chatting.dto.response_dto.BoardResponseDto;
import kr.ac.hansung.cse.board_and_chatting.exception.APIResponse;
import kr.ac.hansung.cse.board_and_chatting.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "게시판 URI", description = "게시판 관련 작업 API 목록입니다.")
public interface BoardSpecification {

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
                },
            responses = {
            @ApiResponse(responseCode = "200", description = "글 불러오기 성공", content = @Content(schema = @Schema(implementation = BoardResponseDto.GeneralArticlesResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = "인증 세션 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "400", description = "쿼리 파라미터(page, size) 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "400", description = "쿼리 파라미터 포맷 잘못됨", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            }
    )
    @GetMapping("/get_articles")
    public ResponseEntity<APIResponse<BoardResponseDto.GeneralArticlesResponseDto>> getArticles(
            @Valid RequestHeaderDto.PagingHeader pagingHeader,
            HttpServletRequest request
    );

    @Operation(summary = "선택한 게시글 보기", description = "선택한 게시글의 ID를 받아 해당 게시글의 내용 및 댓글들을 보여줍니다.",
            parameters = {
            @Parameter(
                    name = "board_id",
                    description = "게시글 ID",
                    in = ParameterIn.PATH,
                    required = true,
                    example = "1"
            ),
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
    @GetMapping("/get_article/{id}")
    public ResponseEntity<?> getArticle(@PathVariable(value = "id") Long id,
                                        @Valid RequestHeaderDto.PagingHeader pagingHeader,
                                        HttpServletRequest request
    );
}
