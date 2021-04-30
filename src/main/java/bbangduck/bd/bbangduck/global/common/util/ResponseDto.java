package bbangduck.bd.bbangduck.global.common.util;

import bbangduck.bd.bbangduck.domain.member.controller.status.MemberResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDto<T> {

    private int status;

    private T data;

    private String message;

    public ResponseDto(MemberResponseStatus memberResponseStatus, T data) {
        this.status= memberResponseStatus.getStatus();
        this.data = data;
        this.message = memberResponseStatus.getMessage();
    }
}
