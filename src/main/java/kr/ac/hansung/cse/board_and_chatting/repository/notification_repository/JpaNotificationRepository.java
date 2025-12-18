package kr.ac.hansung.cse.board_and_chatting.repository.notification_repository;

import kr.ac.hansung.cse.board_and_chatting.entity.Notification;
import kr.ac.hansung.cse.board_and_chatting.entity.User;
import kr.ac.hansung.cse.board_and_chatting.repository.comment_repository.JpaCommentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaNotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserAndIsReadFalse(User user);
}