package kr.ac.hansung.cse.board_and_chatting.service.user_service;

import kr.ac.hansung.cse.board_and_chatting.dto.jpa_dto.board_dto.BoardDto;
import kr.ac.hansung.cse.board_and_chatting.dto.request_dto.UserRequestDto;
import kr.ac.hansung.cse.board_and_chatting.dto.request_parameter_dto.RequestParameterDtoImpl;
import kr.ac.hansung.cse.board_and_chatting.dto.response_dto.BoardResponseDto;
import kr.ac.hansung.cse.board_and_chatting.dto.response_dto.UserResponseDto;
import kr.ac.hansung.cse.board_and_chatting.entity.Board;
import kr.ac.hansung.cse.board_and_chatting.entity.Comment;
import kr.ac.hansung.cse.board_and_chatting.entity.Friend;
import kr.ac.hansung.cse.board_and_chatting.entity.User;
import kr.ac.hansung.cse.board_and_chatting.entity.enums.Authority;
import kr.ac.hansung.cse.board_and_chatting.entity.enums.FriendStatus;
import kr.ac.hansung.cse.board_and_chatting.event.FriendRequestEvent;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.GeneralException;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.LogInException;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.SignUpForException;
import kr.ac.hansung.cse.board_and_chatting.exception.status.ErrorStatus;
import kr.ac.hansung.cse.board_and_chatting.repository.board_repository.BoardRepository;
import kr.ac.hansung.cse.board_and_chatting.repository.comment_repository.CommentRepository;
import kr.ac.hansung.cse.board_and_chatting.repository.friend_repository.JpaFriendRepository;
import kr.ac.hansung.cse.board_and_chatting.repository.user_repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@Primary
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private BoardRepository boardRepository;
    private CommentRepository commentRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private JpaFriendRepository friendRepository;
    private ApplicationEventPublisher applicationEventPublisher;
    private Executor threadPoolTaskExecutor;

    public UserServiceImpl(UserRepository userRepository, ApplicationEventPublisher applicationEventPublisher, JpaFriendRepository friendRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder, CommentRepository commentRepository, BoardRepository boardRepository,
                           @Qualifier("databaseTaskExecutor") Executor threadPoolTaskExecutor) {
        this.userRepository = userRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.friendRepository = friendRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.commentRepository = commentRepository;
        this.boardRepository = boardRepository;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    // 회원가입 진행 서비스 메소드
    @Transactional
    public User signUpService(UserRequestDto userDto) throws IOException {
        Optional<User> userOptional = userRepository.findByUserId(userDto.getUserId());

        // 이미 존재하는 회원일 경우 예외처리
        if (userOptional.isPresent()) {
            throw new SignUpForException(ErrorStatus.ALREADY_EXISTS_USER);
        }

        byte[] pictureBytes = null;
        if (userDto.getUserPicture() != null && !userDto.getUserPicture().isEmpty()) {
            pictureBytes = userDto.getUserPicture().getBytes();
        }

        if (userDto.getUserId().equals("ADMIN")) {
            User user = User.builder()
                    .nickname(userDto.getNickname())
                    .userId(userDto.getUserId())
                    .password(bCryptPasswordEncoder.encode(userDto.getPassword()))
                    .authority(Authority.ADMIN)
                    .userPicture(pictureBytes)
                    .build();
            userRepository.save(user);
            return user;
        } else {
            User user = User.builder()
                    .nickname(userDto.getNickname())
                    .userId(userDto.getUserId())
                    .password(bCryptPasswordEncoder.encode(userDto.getPassword()))
                    .authority(Authority.USER)
                    .userPicture(pictureBytes)
                    .build();
            userRepository.save(user);
            return user;
        }
    }

    @Transactional
    public User loginService(UserRequestDto.LoginDto userDto) {
        Optional<User> userOptional = userRepository.findByUserId(userDto.getUserId());

        // DB에 해당 User 정보가 없다면 예외처리
        userOptional.orElseThrow(() -> new LogInException(ErrorStatus.NOT_EXISTING_USER));

        userOptional.ifPresent(user -> {
            if (!bCryptPasswordEncoder.matches(userDto.getPassword(), user.getPassword())) {
                throw new LogInException(ErrorStatus.WRONG_PASSWORD);
            }
        });

        // user 객체 반환
        return userOptional.get();
    }

    @Override
    @Transactional
    public void friendRequest(String from, String to) {

        User fromUser = userRepository.findByNickname(from)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_FRIEND_WITH_THE_NICKNAME));

        User toUser = userRepository.findByNickname(to)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_FRIEND_WITH_THE_NICKNAME));

        if (from.equals(to)) throw new GeneralException(ErrorStatus.ENABLE_FRIEND_BY_MYSELF);

        if (friendRepository.existsByRequesterAndReceiver(fromUser, toUser)) throw new GeneralException(ErrorStatus.ALREADY_FRIEND_REQUESTED);

        if (friendRepository.existsByRequesterAndReceiver(fromUser, toUser) && friendRepository.existsByRequesterAndReceiver(toUser, fromUser))
            throw new GeneralException(ErrorStatus.ALREADY_FRIEND);

        Friend friend = Friend.create(fromUser, toUser);

        friendRepository.save(friend);

        applicationEventPublisher.publishEvent(new FriendRequestEvent(friend));
    }

    @Transactional(readOnly = true)
    public UserResponseDto.ViewMyProfileResponseDto getMyProfile(User user, RequestParameterDtoImpl.ViewMyProfileParameterDto viewMyProfileParameterDto) {

        // 유저 닉네임
        String nickname = user.getNickname();

        int postPage = viewMyProfileParameterDto.getPostPage();
        int postSize = viewMyProfileParameterDto.getPostSize();

        Pageable pageable = PageRequest.of(postPage, postSize);

        // 게시글 불러오기 쿼리 실행(비동기)
        CompletableFuture<Page<Board>> pageBoardCompletableFuture = CompletableFuture.supplyAsync(() ->
                boardRepository.findAllByUserIdCustom(user.getId(), pageable), threadPoolTaskExecutor);

        // 댓글 불러오기 쿼리 실행(비동기)
        int commentSize = viewMyProfileParameterDto.getCommentSize();
        int commentPage = viewMyProfileParameterDto.getCommentPage();
        Pageable commentPageable = PageRequest.of(commentPage, commentSize);
        CompletableFuture<Page<Comment>> pageCommentCompletableFuture = CompletableFuture.supplyAsync(() ->
                commentRepository.findCommentByUserIdCustom(user.getId(), commentPageable), threadPoolTaskExecutor);

        // 친구 불러오기 쿼리 실행(비동기)
        int friendSize = viewMyProfileParameterDto.getFriendSize();
        int friendPage = viewMyProfileParameterDto.getFriendPage();
        Pageable friendPageable = PageRequest.of(friendPage, friendSize);

        CompletableFuture<Page<Friend>> pageFriendCompletableFuture = CompletableFuture.supplyAsync(() ->
                friendRepository.findByRequesterIdAndFriendStatus(user.getId(), FriendStatus.FRIEND, friendPageable), threadPoolTaskExecutor);

        return CompletableFuture.allOf(
                        pageBoardCompletableFuture,
                        pageCommentCompletableFuture,
                        pageFriendCompletableFuture
                )
                .thenApply(v -> {
                    // 1. 비동기 작업 결과물(Page 객체) 추출 (변수명 차별화)
                    Page<Board> boardResultPage = pageBoardCompletableFuture.join();
                    Page<Comment> commentResultPage = pageCommentCompletableFuture.join();
                    Page<Friend> friendResultPage = pageFriendCompletableFuture.join();

                    // 2. DTO 빌더를 이용한 매핑
                    return UserResponseDto.ViewMyProfileResponseDto.builder()
                            .nickname(nickname) // 상단에서 선언한 유저 닉네임 변수 사용

                            // 게시글 데이터 조립
                            .totalArticlePages((long) boardResultPage.getTotalPages())
                            .articles(boardResultPage.getContent().stream()
                                    .map(b -> b.getTitle())
                                    .collect(Collectors.toList())
                            )

                            // 댓글 데이터 조립
                            .totalCommentPages((long) commentResultPage.getTotalPages())
                            .comments(commentResultPage.getContent()
                                    .stream().map(c -> c.getContent())
                                    .collect(Collectors.toList())
                            ) // 엔티티 리스트 그대로 매핑

                            // 친구 데이터 조립 (엔티티 리스트에서 이름만 추출)
                            .totalFriendPages((long) friendResultPage.getTotalPages())
                            .friendNames(friendResultPage.getContent().stream()
                                    .map(friend -> {
                                        if (friend.getRequester().getNickname().equals(nickname)) return friend.getRequester().getNickname();
                                        else return friend.getReceiver().getNickname();
                                    }) // Friend 엔티티 구조에 맞춰 조정 필요
                                    .collect(Collectors.toList()))
                            .build();
                })
                .join(); // 모든 비동기 처리가 완료된 최종 DTO 객체 반환 // 마지막에 최종 DTO 반환

    }
}
