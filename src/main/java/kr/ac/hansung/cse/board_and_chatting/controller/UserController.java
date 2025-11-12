package kr.ac.hansung.cse.board_and_chatting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.ac.hansung.cse.board_and_chatting.controller.specification.UserSpecification;
import kr.ac.hansung.cse.board_and_chatting.dto.request_dto.UserRequestDto;
import kr.ac.hansung.cse.board_and_chatting.dto.request_dto.UserRequestDto;
import kr.ac.hansung.cse.board_and_chatting.dto.response_dto.UserResponse;
import kr.ac.hansung.cse.board_and_chatting.dto.response_dto.UserResponseDto;
import kr.ac.hansung.cse.board_and_chatting.entity.User;
import kr.ac.hansung.cse.board_and_chatting.exception.APIResponse;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.SignUpForException;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.ValidationException;
import kr.ac.hansung.cse.board_and_chatting.exception.status.ErrorStatus;
import kr.ac.hansung.cse.board_and_chatting.exception.status.SuccessStatus;
import kr.ac.hansung.cse.board_and_chatting.service.user_service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/api")
public class UserController implements UserSpecification {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    public ResponseEntity<APIResponse<UserResponseDto.SignUpResponseDto>> signUp(@Valid @ModelAttribute UserRequestDto userDto, BindingResult bindingResult, HttpServletRequest request) throws IOException {
        log.info(userDto.toString());

        // 유효성 검사 실패 후 예외 처리
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult, ErrorStatus.NOT_SUFFICIENT_DATA_FOR_SIGN_UP);
        }

        User user = userService.signUpService(userDto);
        // user 객체가 NULL 값을 가질 일은 없지만 만약 갖게 된다면 예외 처리
        if (user == null) {
            throw new SignUpForException(ErrorStatus.INTERNAL_BAD_REQUEST);
        }

        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        UserResponseDto.SignUpResponseDto signUpResponseDto = UserResponseDto.SignUpResponseDto.builder()
                .userId(user.getUserId())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .build();

        return APIResponse.toResponseEntity(
                APIResponse.<UserResponseDto.SignUpResponseDto>builder()
                        .status(SuccessStatus.SIGN_UP_SUCCESS.getStatus())
                        .code(SuccessStatus.SIGN_UP_SUCCESS.getCode())
                        .message(SuccessStatus.SIGN_UP_SUCCESS.getMessage())
                        .result(signUpResponseDto)
                        .build()
        );

    }

    public ResponseEntity<APIResponse<UserResponseDto.LoginResponseDto>> login(@Valid @RequestBody UserRequestDto.LoginDto loginDto, BindingResult bindingResult, HttpServletRequest request) {
        log.info(loginDto.toString());
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult, ErrorStatus.NOT_SUFFICIENT_DATA_FOR_LOG_IN);
        }

        // 로그인 처리
        User user = userService.loginService(loginDto);

        // 세션 새로 생성
        HttpSession session = request.getSession(true);  // 없으면 새로 만듦
        session.setAttribute("user", user);

        UserResponseDto.LoginResponseDto logInResponseDto = UserResponseDto.LoginResponseDto.builder()
                .userId(user.getUserId())
                .password(user.getPassword())
                .build();

        return APIResponse.toResponseEntity(
                APIResponse.<UserResponseDto.LoginResponseDto>builder()
                        .status(SuccessStatus.LOG_IN_SUCCESS.getStatus())
                        .code(SuccessStatus.LOG_IN_SUCCESS.getCode())
                        .message(SuccessStatus.LOG_IN_SUCCESS.getMessage())
                        .result(logInResponseDto)
                        .build()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // 세션 있으면 정상적으로 만료
        }

        return APIResponse.toResponseEntity(
                APIResponse.builder()
                        .status(SuccessStatus.LOG_OUT_SUCCESS.getStatus())
                        .code(SuccessStatus.LOG_OUT_SUCCESS.getCode())
                        .message(SuccessStatus.LOG_OUT_SUCCESS.getMessage())
                        .build()
        );
    }
}
