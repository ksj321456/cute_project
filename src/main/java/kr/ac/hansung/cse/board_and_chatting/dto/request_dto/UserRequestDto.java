package kr.ac.hansung.cse.board_and_chatting.dto.request_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDto {

    @Schema(description = "닉네임", example = "ksj321456")
    @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    private String nickname;

    @Schema(description = "비밀번호", example = "1234")
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;

    @Schema(description = "아이디", example = "ksj321456")
    @NotBlank(message = "아이디는 필수 입력 항목입니다.")
    private String userId;

    // 사용자 프로필 사진
    @Schema(description = "프로필 사진", type = "string", format = "binary")
    private MultipartFile userPicture;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LoginDto {

        @Schema(description = "아이디", example = "ksj321456")
        @NotBlank(message = "아이디는 필수 입력 항목입니다.")
        private String userId;

        @Schema(description = "비밀번호", example = "1234")
        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        private String password;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FriendRequestDto {

        @Schema(description = "친구 추가하고자 하는 닉네임", example = "ksj")
        @NotBlank(message = "친구 요청 닉네임을 입력해주세요.")
        private String nickname;
    }

}
