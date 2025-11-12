package kr.ac.hansung.cse.board_and_chatting.configuration;

import kr.ac.hansung.cse.board_and_chatting.entity.Board;
import kr.ac.hansung.cse.board_and_chatting.entity.Comment;
import kr.ac.hansung.cse.board_and_chatting.entity.User;
import kr.ac.hansung.cse.board_and_chatting.entity.enums.Authority;
import kr.ac.hansung.cse.board_and_chatting.entity.enums.Category;
import kr.ac.hansung.cse.board_and_chatting.repository.board_repository.BoardJpaRepository;
import kr.ac.hansung.cse.board_and_chatting.repository.comment_repository.CommentRepository;
import kr.ac.hansung.cse.board_and_chatting.repository.user_repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

//@Component
@Slf4j
@RequiredArgsConstructor
public class ProjectInitializer {

    private final BoardJpaRepository boardJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final CommentRepository commentRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        long startTime = System.currentTimeMillis();
        User user = User.builder()
                .nickname("관리자")
                .userId("root")
                .password(bCryptPasswordEncoder.encode("1234"))
                .authority(Authority.ADMIN)
                .createdAt(LocalDateTime.now())
                .build();
        userJpaRepository.save(user);

        for (int i = 0; i < 30000; i++) {
            Board board = Board.builder()
                    .title("제목 " + i)
                    .category(Category.FREE)
                    .content("내용 " + i)
                    .user(user)
                    .like(0L)
                    .dislike(0L)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            log.info("Creating board with title: " + board.getTitle());
            boardJpaRepository.save(board);

            Comment comment = Comment.builder()
                    .content("댓글 " + i)
                    .board(board)
                    .user(user)
                    .build();

            commentRepository.save(comment);
        }

        User user2 = User.builder()
                .nickname("user2")
                .userId("user")
                .password(bCryptPasswordEncoder.encode("1234"))
                .authority(Authority.USER)
                .createdAt(LocalDateTime.now())
                .build();

        userJpaRepository.save(user2);

        for (int i = 0; i < 30000; i++) {
            Board board = Board.builder()
                    .title("제에목 " + i)
                    .category(Category.FREE)
                    .content("내애용 " + i)
                    .user(user2)
                    .like(0L)
                    .dislike(0L)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            boardJpaRepository.save(board);

            Comment comment = Comment.builder()
                    .content("대앳글 " + i)
                    .board(board)
                    .user(user)
                    .build();

            commentRepository.save(comment);
        }

        long endTime = System.currentTimeMillis();
        log.info("Take time: " + (endTime - startTime) + "ms");
    }
}