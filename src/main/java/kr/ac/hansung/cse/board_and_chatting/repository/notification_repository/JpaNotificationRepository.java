package kr.ac.hansung.cse.board_and_chatting.repository.notification_repository;

import kr.ac.hansung.cse.board_and_chatting.entity.Notification;
import kr.ac.hansung.cse.board_and_chatting.entity.User;
import kr.ac.hansung.cse.board_and_chatting.repository.comment_repository.JpaCommentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaNotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserAndIsReadFalse(User user);

    @Query("SELECT n FROM Notification n JOIN FETCH n.user WHERE n.user = :user")
    List<Notification> findByUser(@Param("user") User user);


    // 쿼리 전송 후, 영속성 컨텍스트 clear(DB와 불일치 문제 때문에)
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id IN :ids")
    void markAsReadByIds(@Param("ids") List<Long> ids);
}