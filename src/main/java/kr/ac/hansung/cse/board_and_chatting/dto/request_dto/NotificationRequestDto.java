package kr.ac.hansung.cse.board_and_chatting.dto.request_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class NotificationRequestDto {

    @Getter
    @AllArgsConstructor
    public static class AcceptFriendRequestDto {
        private Long notificationId;
        private String requesterNickname;
    }
}
