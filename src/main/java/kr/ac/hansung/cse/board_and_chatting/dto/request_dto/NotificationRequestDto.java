package kr.ac.hansung.cse.board_and_chatting.dto.request_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NotificationRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AcceptFriendRequestDto {

        @NotNull(message = "알림 ID 전송 해야합니다.")
        private Long notificationId;

        @NotBlank(message = "초기 친구 요청을 보낸 유저의 닉네임이 필요합니다.")
        private String requesterNickname;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RejectFriendRequestDto {
        @NotNull(message = "알림 ID 전송 해야합니다.")
        private Long notificationId;

        @NotBlank(message = "초기 친구 요청을 보낸 유저의 닉네임이 필요합니다.")
        private String requesterNickname;
    }
}
