package kr.ac.hansung.cse.board_and_chatting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.ac.hansung.cse.board_and_chatting.controller.specification.UserSpecification;
import kr.ac.hansung.cse.board_and_chatting.dto.request_dto.UserRequestDto;
import kr.ac.hansung.cse.board_and_chatting.dto.request_dto.UserRequestDto;
import kr.ac.hansung.cse.board_and_chatting.dto.request_parameter_dto.RequestParameterDtoImpl;
import kr.ac.hansung.cse.board_and_chatting.dto.response_dto.EmptyResponse;
import kr.ac.hansung.cse.board_and_chatting.dto.response_dto.UserResponse;
import kr.ac.hansung.cse.board_and_chatting.dto.response_dto.UserResponseDto;
import kr.ac.hansung.cse.board_and_chatting.entity.User;
import kr.ac.hansung.cse.board_and_chatting.exception.APIResponse;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.AuthenticationException;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.GeneralException;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.SignUpForException;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.ValidationException;
import kr.ac.hansung.cse.board_and_chatting.exception.status.ErrorStatus;
import kr.ac.hansung.cse.board_and_chatting.exception.status.SuccessStatus;
import kr.ac.hansung.cse.board_and_chatting.service.authentication_service.SessionService;
import kr.ac.hansung.cse.board_and_chatting.service.user_service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/api")
public class UserController implements UserSpecification {

    private UserService userService;
    private SessionService sessionService;
    private EmptyResponse emptyResponse;

    @Autowired
    public UserController(UserService userService, SessionService sessionService, EmptyResponse emptyResponse) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.emptyResponse = emptyResponse;
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

        sessionService.setSession(request, user);

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
//        HttpSession session = request.getSession(true);  // 없으면 새로 만듦
//        session.setAttribute("user", user);
        sessionService.setSession(request, user);

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

    public ResponseEntity<APIResponse<EmptyResponse>> logout(HttpServletRequest request, HttpServletResponse response) {

        sessionService.logOut(sessionService.getSession(request));

        // 브라우저에 남아 있는 JSESSION 세션 쿠키 삭제
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return APIResponse.toResponseEntity(
                APIResponse.<EmptyResponse>builder()
                        .status(SuccessStatus.LOG_OUT_SUCCESS.getStatus())
                        .code(SuccessStatus.LOG_OUT_SUCCESS.getCode())
                        .message(SuccessStatus.LOG_OUT_SUCCESS.getMessage())
                        .result(emptyResponse)
                        .build()
        );
    }

    public ResponseEntity<APIResponse<EmptyResponse>> home(HttpServletRequest request) {

        if (sessionService.getSession(request) != null) {
            return APIResponse.toResponseEntity(
                    APIResponse.<EmptyResponse>builder()
                            .status(SuccessStatus.SESSION_TEST_SUCCESS.getStatus())
                            .code(SuccessStatus.SESSION_TEST_SUCCESS.getCode())
                            .message(SuccessStatus.SESSION_TEST_SUCCESS.getMessage())
                            .result(emptyResponse)
                            .build()
            );
        } else {
            return APIResponse.toResponseEntity(
                    APIResponse.<EmptyResponse>builder()
                            .status(SuccessStatus.NO_SESSION.getStatus())
                            .code(SuccessStatus.NO_SESSION.getCode())
                            .message(SuccessStatus.NO_SESSION.getMessage())
                            .result(emptyResponse)
                            .build()
            );
        }
    }


    @Override
    public ResponseEntity<APIResponse<EmptyResponse>> requestFriend(@Valid @RequestBody UserRequestDto.FriendRequestDto friendRequestDto,
                                                                    BindingResult bindingResult,
                                                                    HttpServletRequest request) {

        if (sessionService.getSession(request) == null) throw new AuthenticationException(ErrorStatus.NO_AUTHENTICATION);

        if (bindingResult.hasErrors()) throw new GeneralException(ErrorStatus.EMPTY_NICKNAME_FOR_FRIEND_REQUEST);

        User user = (User) sessionService.getSession(request).getAttribute("user");

        String from = user.getNickname();

        String to = friendRequestDto.getNickname();

        // 서비스 로직에 from, to 넘겨 주기
        userService.friendRequest(from, to);

        return APIResponse.toResponseEntity(
                APIResponse.<EmptyResponse>builder()
                        .status(SuccessStatus.FRIEND_REQUEST_SUCCESS.getStatus())
                        .code(SuccessStatus.FRIEND_REQUEST_SUCCESS.getCode())
                        .message(SuccessStatus.FRIEND_REQUEST_SUCCESS.getMessage())
                        .build()
        );
    }

    @Override
    public ResponseEntity<APIResponse<UserResponseDto.ViewMyProfileResponseDto>> viewMyProfile(RequestParameterDtoImpl.ViewMyProfileParameterDto viewMyProfileParameterDto,
                                                                                               HttpServletRequest request) {

        if (sessionService.getSession(request) == null) throw new AuthenticationException(ErrorStatus.NO_AUTHENTICATION);

        User user = (User) sessionService.getSession(request).getAttribute("user");

        // 자신의 프로필 조회 시 필요한 데이터들 조회하기, 비동기로 실행
        // ViewMyProfileParameterDto를 매개변수로 전달해, 페이징 쿼리 실행
        UserResponseDto.ViewMyProfileResponseDto viewMyProfileResponseDto = userService.getMyProfile(user, viewMyProfileParameterDto);

        return APIResponse.toResponseEntity(
                APIResponse.<UserResponseDto.ViewMyProfileResponseDto>builder()
                        .status(SuccessStatus.VIEW_MY_PROFILE_SUCCESS.getStatus())
                        .code(SuccessStatus.VIEW_MY_PROFILE_SUCCESS.getCode())
                        .message(SuccessStatus.VIEW_MY_PROFILE_SUCCESS.getMessage())
                        .result(viewMyProfileResponseDto)
                        .build()
        );
    }
}
