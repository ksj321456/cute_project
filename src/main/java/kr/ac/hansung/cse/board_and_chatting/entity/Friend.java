package kr.ac.hansung.cse.board_and_chatting.entity;

import jakarta.persistence.*;
import kr.ac.hansung.cse.board_and_chatting.entity.enums.FriendStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Friend implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "friend_id")
    private Long id;

    // 실제로 친구가 맺어졌을 때는 A -> B, B -> A 관계 다 테이블에 저장되어야 함.
    // 친구 요청을 보낸 user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private User requester;

    // 친구 요청을 받은 user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    // 친구 상태(요청, 친구)
    @Enumerated(EnumType.STRING)
    private FriendStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Friend create(User requester, User receiver) {
        Friend friend = new Friend();
        friend.requester = requester;
        friend.receiver = receiver;
        friend.status = FriendStatus.REQUESTED;
        return friend;
    }

    @Override
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
