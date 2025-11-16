package kr.ac.hansung.cse.board_and_chatting.dto.request_parameter_dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class RequestParameterDtoImpl {

    @Getter
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    @Setter
    public static class PagingHeader implements RequestParameterDto {
        @NotNull(message = "page 파라미터는 필수입니다.")
        @Min(value = 0, message = "page 파라미터의 값은 0 이상이어야 합니다.")
        private Integer page;

        @NotNull(message = "size 파라미터는 필수입니다.")
        @Min(value = 1, message = "size 파라미터의 값은 1 이상이어야 합니다.")
        private Integer size;
    }
}
