package kr.ac.hansung.cse.board_and_chatting.service.authentication_service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.ac.hansung.cse.board_and_chatting.entity.User;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.ValidationException;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    public HttpSession getSession(HttpServletRequest request) {
        // 있으면 session 반환, 없으면 null
        return request.getSession(false);
    }

    public HttpSession setSession(HttpServletRequest request, User user) {
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        return session;
    }

    public void logOut(HttpSession session) {
        session.invalidate();
    }
}
