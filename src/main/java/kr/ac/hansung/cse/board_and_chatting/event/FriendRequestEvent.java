package kr.ac.hansung.cse.board_and_chatting.event;

import kr.ac.hansung.cse.board_and_chatting.entity.Friend;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendRequestEvent {
    private Friend friend;
}
