package bbangduck.bd.bbangduck.global.common;

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

    public ResponseDto(ResponseStatus responseStatus, T data) {
        this.status= responseStatus.getStatus();
        this.data = data;
        this.message = responseStatus.getMessage();
    }
}
