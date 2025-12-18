package kr.ac.hansung.cse.board_and_chatting.service.user_service;

import kr.ac.hansung.cse.board_and_chatting.dto.request_dto.UserRequestDto;
import kr.ac.hansung.cse.board_and_chatting.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Transactional(readOnly = true)
public interface UserService {
    public User signUpService(UserRequestDto userDto) throws IOException;

    public User loginService(UserRequestDto.LoginDto userDto);

    void friendRequest(String from, String to);
}
