package bbangduck.bd.bbangduck.global.config.security;

import bbangduck.bd.bbangduck.global.common.ResponseDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * API 요청 시 회원에 대한 인증은 완료되었지만, 해당 API 리소스에 대한 접근 권한을 인가받지 못한 회원이
 * 리소스에 접근하는 경우 AccessDeniedHandler 를 통해 관리된다.
 * 이에 대한 응답 status code, message 등을 Custom 하게 관리하기 위해 구현한 구현체
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try (ServletOutputStream os = response.getOutputStream()) {
            objectMapper.writeValue(os,new ResponseDto<>(ResponseStatus.FORBIDDEN, null));
            os.flush();
        }
    }
}
