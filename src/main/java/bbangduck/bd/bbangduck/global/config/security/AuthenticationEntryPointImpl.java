package bbangduck.bd.bbangduck.global.config.security;

import bbangduck.bd.bbangduck.global.common.ResponseDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 작성자 : 정구민 <br><br>
 * <p>
 * API 요청 시 인증이 실패할 경우 해당 결과가 AuthenticationEntryPoint 를 통해 관리되는데,
 * 이에 대한 응답 status code, message 등을 Custom 하게 관리하기 위해 구현한 구현체
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error("인증 실패 Error 발생!!");
        log.error("message : {}", authException.getMessage());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try (ServletOutputStream os = response.getOutputStream()) {
            objectMapper.writeValue(os, new ResponseDto<>(ResponseStatus.UNAUTHORIZED, null));
            os.flush();
        }
    }


}
