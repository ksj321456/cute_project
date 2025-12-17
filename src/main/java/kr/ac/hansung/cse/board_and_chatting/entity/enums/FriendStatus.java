package kr.ac.hansung.cse.board_and_chatting.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FriendStatus {
    REQUESTED("요청"), FRIEND("친구");

    private final String status;

    @JsonValue
    public String getStatus() {
        return status;
    }

    @JsonCreator
    public static FriendStatus fromStatus(String status) {
        for (FriendStatus friendStatus : FriendStatus.values()) {
            if (friendStatus.status.equals(status)) {
                return friendStatus;
            }
        }
        throw new IllegalArgumentException(status);
    }
}
