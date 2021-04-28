package bbangduck.bd.bbangduck.security;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class JwtAccessExceptionResponseDto {
    private HttpStatus httpStatus;
    private int statusCode;
    private String message;

    public JwtAccessExceptionResponseDto(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.statusCode = httpStatus.value();
        this.message = message;
    }
}
