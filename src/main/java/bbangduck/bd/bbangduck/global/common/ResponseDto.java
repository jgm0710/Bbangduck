package bbangduck.bd.bbangduck.global.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

/**
 * 작성자 : 정구민 <br><br>
 *
 * API 요청 시 응답 값으로 HttpStatus 와 별개로 응답을 구분하기 위한 Status,
 * 응답 Body 에 담을 Data, 응답에 대한 간단한 Message 를 담기 위한 Dto
 */
@JsonSerialize
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseDto<T> {

    private int status;

    private T data;

    private String message;

    @Builder
    public ResponseDto(ResponseStatus responseStatus, T data) {
        this.status= responseStatus.getStatus();
        this.data = data;
        this.message = responseStatus.getMessage();
    }

    @Builder
    public ResponseDto(int status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
