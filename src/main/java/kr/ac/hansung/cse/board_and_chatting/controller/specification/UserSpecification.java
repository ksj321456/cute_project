package kr.ac.hansung.cse.board_and_chatting.controller.specification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.ac.hansung.cse.board_and_chatting.dto.request_dto.UserRequestDto;
import kr.ac.hansung.cse.board_and_chatting.dto.request_parameter_dto.RequestParameterDtoImpl;
import kr.ac.hansung.cse.board_and_chatting.dto.response_dto.EmptyResponse;
import kr.ac.hansung.cse.board_and_chatting.dto.response_dto.UserResponseDto;
import kr.ac.hansung.cse.board_and_chatting.exception.APIResponse;
import kr.ac.hansung.cse.board_and_chatting.exception.ErrorResponse;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping(path = "/logout")
    @Operation(summary = "로그아웃", description = "클라이언트로부터 JSESSIONID 세션 쿠키를 받아 해당 세션 무효화 진행(반환 DTO 없음)",
        responses = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content(schema = @Schema(implementation = EmptyResponse.class))),
        }
    )
    ResponseEntity<APIResponse<EmptyResponse>> logout(HttpServletRequest request, HttpServletResponse response);

    @GetMapping(path = "/home")
    @Operation(summary = "홈페이지에 접근 시, 세션 검사하여, 통과하면 게시글 화면으로 이동, 그렇지 않으면 알림 제공(반환 DTO 없음)",
        responses = {
            @ApiResponse(responseCode = "200", description = "세션 검사 성공", content = @Content(schema = @Schema(implementation = EmptyResponse.class))),
                @ApiResponse(responseCode = "401", description = "세션 검사 실패", content = @Content(schema = @Schema(implementation = EmptyResponse.class))),
        }
    )
    ResponseEntity<APIResponse<EmptyResponse>> home(HttpServletRequest request);


    @PostMapping("/request_friend")
    @Operation(summary = "친구 요청", description = "친구 요청 보내기 -> 이벤트로 던져줘서 이벤트 리스너(구독자)가 가로채서 대신 수행하는 형식")
    ResponseEntity<APIResponse<EmptyResponse>> requestFriend(UserRequestDto.FriendRequestDto friendRequestDto, BindingResult bindingResult, HttpServletRequest request);

    @GetMapping("/view_my_profile")
    @Operation(summary = "자기 프로필 보기", description = "자기 프로필 내용에는 User Entity, 자신이 쓴 글, 댓글 그리고 친구 목록을 보여줍니다.")
    public ResponseEntity<APIResponse<UserResponseDto.ViewMyProfileResponseDto>> viewMyProfile(RequestParameterDtoImpl.ViewMyProfileParameterDto viewMyProfileParameterDto,
                                                                                               HttpServletRequest request);

}
