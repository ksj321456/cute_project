package kr.ac.hansung.cse.board_and_chatting;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.hansung.cse.board_and_chatting.dto.request_dto.BoardRequestDto;
import kr.ac.hansung.cse.board_and_chatting.entity.User;
import kr.ac.hansung.cse.board_and_chatting.entity.enums.Authority;
import kr.ac.hansung.cse.board_and_chatting.entity.enums.Category;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.AuthenticationException;
import kr.ac.hansung.cse.board_and_chatting.repository.user_repository.UserRepository;
import kr.ac.hansung.cse.board_and_chatting.service.board_service.BoardService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class CreateArticleTest {

    private BoardService boardService;
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    public CreateArticleTest(BoardService boardService, UserRepository userRepository) {
        this.boardService = boardService;
        this.userRepository = userRepository;
    }

    @Test
    @DisplayName("1. 게시글 작성 성공")
    void createArticle_01() {
        User user = User.builder()
                .userId("test1")
                .password("test_pw")
                .nickname("tester")
                .authority(Authority.USER)
                .createdAt(LocalDateTime.now())
                .build();

        user = userRepository.save(user);

        BoardRequestDto.CreateArticleRequest createArticleRequest = BoardRequestDto.CreateArticleRequest.builder()
                .title("제목1")
                .content("내용1")
                .category(Category.FREE)
                .build();

        boardService.saveArticle(createArticleRequest, user);
    }

    @Test
    @DisplayName("2. 게시글 작성 실패(중복된 제목)")
    void createArticle_02() {
        User user = userRepository.findByUserId("root").get();
        System.out.println(user);

        BoardRequestDto.CreateArticleRequest createArticleRequest = BoardRequestDto.CreateArticleRequest.builder()
                .title("제목1")
                .content("내용1")
                .category(Category.FREE)
                .build();

        Assertions.assertThrows(AuthenticationException.class, () -> boardService.saveArticle(createArticleRequest, user));
    }

    @Test
    @DisplayName("Validation 실패 - 필수 필드 누락")
    void createArticle_validationFail() throws Exception {
        // given: 잘못된 요청 본문 (title 누락)
        String jsonRequest = """
                {
                    "content": "내용1",
                    "category": "FREE"
                }
                """;

        mockMvc.perform(post("/api/create-article")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .sessionAttr("user", getFakeUser()))  // 세션에 유저도 넣어줌
                .andExpect(status().isBadRequest());
    }

    @Test
    void runAsync() throws InterruptedException, ExecutionException {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("Thread: " + Thread.currentThread().getName());
        });

        future.get();
        System.out.println("Thread: " + Thread.currentThread().getName());
    }

    @Test
    void supplyAsync() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            return "Thread: " + Thread.currentThread().getName();
        });

        System.out.println(future.get());
        System.out.println("Thread: " + Thread.currentThread().getName());
    }

    @Test
    void thenApply() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            return "Thread: " + Thread.currentThread().getName();
        }).thenApply(s -> s.toUpperCase());
        System.out.println(future.get());
    }

    @Test
    void thenAccept() throws InterruptedException, ExecutionException {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            return "Thread: " + Thread.currentThread().getName();
        }).thenAccept(s -> System.out.println(s.toUpperCase()));

        future.get();
    }

    @Test
    void allOf() throws ExecutionException, InterruptedException {
        CompletableFuture<String> hello = CompletableFuture.supplyAsync(() -> {
            return "Hello";
        });

        CompletableFuture<String> mangKyu = CompletableFuture.supplyAsync(() -> {
            return "MangKyu";
        });

        List<CompletableFuture<String>> futures = List.of(hello, mangKyu);

        CompletableFuture<List<String>> result = CompletableFuture.allOf(hello, mangKyu)
                .thenApply(v -> futures.stream().
                        map(CompletableFuture::join).
                        collect(Collectors.toList()));

        result.get().forEach(System.out::println);
    }

    private User getFakeUser() {
        return User.builder()
                .id(1L)
                .userId("testuser")
                .password("pw")
                .nickname("tester")
                .authority(Authority.USER)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
