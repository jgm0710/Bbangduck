package bbangduck.bd.bbangduck.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, ObjectMapper objectMapper) {
        super(authenticationManager);
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        super.doFilterInternal(request, response, chain);
        String token = jwtTokenProvider.resolveToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("JWT Token 을 통한 인증 성공 {}", authentication.getPrincipal());

            chain.doFilter(request, response);
        } else {
            JwtAccessExceptionResponseDto jwtAccessExceptionResponseDto =
                    new JwtAccessExceptionResponseDto(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다 - Access Token 이 유효하지 않습니다");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(jwtAccessExceptionResponseDto.getStatusCode());
            log.error("JWT Token 을 통한 인증 실패 - 401 응답");

            try (ServletOutputStream outputStream = response.getOutputStream()) {
                objectMapper.writeValue(outputStream, jwtAccessExceptionResponseDto);
                outputStream.flush();
            }
        }
    }
}
