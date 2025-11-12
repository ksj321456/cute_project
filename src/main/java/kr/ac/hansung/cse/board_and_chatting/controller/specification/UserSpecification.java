package kr.ac.hansung.cse.board_and_chatting.controller.specification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.ac.hansung.cse.board_and_chatting.dto.request_dto.UserRequestDto;
import kr.ac.hansung.cse.board_and_chatting.dto.response_dto.UserResponseDto;
import kr.ac.hansung.cse.board_and_chatting.exception.APIResponse;
import kr.ac.hansung.cse.board_and_chatting.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.print.attribute.standard.Media;
import java.io.IOException;

@Tag(name = "유저 API", description = "유저 관련 작업 API 목록입니다.")
public interface UserSpecification {

    @PostMapping(path = "/signup",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "회원가입", description = "아이디, 비밀번호, 닉네임, 프로필 사진(선택)을 받아 회원가입 처리를 합니다.",
                responses = {
                    @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = UserResponseDto.SignUpResponseDto.class))),
                        @ApiResponse(responseCode = "404", description = "회원가입 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "서버 내부 에러", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                }
    )
    ResponseEntity<APIResponse<UserResponseDto.SignUpResponseDto>> signUp(@Parameter(description = "회원가입 데이터")
            @Valid @ModelAttribute UserRequestDto userDto, BindingResult bindingResult, HttpServletRequest request) throws IOException;

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "로그인", description = "아이디, 비밀번호를 받아 로그인 처리를 합니다.",
                responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = UserResponseDto.LoginResponseDto.class))),
                        @ApiResponse(responseCode = "404", description = "로그인 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "서버 내부 에러", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                }
    )
    ResponseEntity<APIResponse<UserResponseDto.LoginResponseDto>> login(@Valid @RequestBody UserRequestDto.LoginDto loginDto,
                                                                        BindingResult bindingResult,
                                                                        HttpServletRequest request);
}
