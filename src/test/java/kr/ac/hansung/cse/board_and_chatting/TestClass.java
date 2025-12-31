package kr.ac.hansung.cse.board_and_chatting;

import kr.ac.hansung.cse.board_and_chatting.dto.request_parameter_dto.RequestParameterDtoImpl;
import kr.ac.hansung.cse.board_and_chatting.dto.response_dto.UserResponseDto;
import kr.ac.hansung.cse.board_and_chatting.entity.User;
import kr.ac.hansung.cse.board_and_chatting.repository.user_repository.UserRepository;
import kr.ac.hansung.cse.board_and_chatting.service.user_service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class TestClass {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void viewMyProfile() throws InterruptedException {
        int threadCount = 5;
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch endSignal = new CountDownLatch(threadCount);
        User user = userRepository.findById(1L).get();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        List<UserResponseDto.ViewMyProfileResponseDto> viewMyProfileResponseDtoList = new ArrayList<>();

        RequestParameterDtoImpl.ViewMyProfileParameterDto viewMyProfileParameterDto = RequestParameterDtoImpl.ViewMyProfileParameterDto.builder()
                .postPage(0)
                .postSize(20)
                .commentPage(0)
                .commentSize(20)
                .friendPage(0)
                .friendSize(20)
                .build();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startSignal.await();

                    viewMyProfileResponseDtoList.add(userService.getMyProfile(user, viewMyProfileParameterDto));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    endSignal.countDown();
                }
            });
        }

        startSignal.countDown();

        endSignal.await();
        executorService.shutdown();

        System.out.println(viewMyProfileResponseDtoList.size());
    }
}
