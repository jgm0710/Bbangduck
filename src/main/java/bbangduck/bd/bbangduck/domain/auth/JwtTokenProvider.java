package bbangduck.bd.bbangduck.domain.auth;

import bbangduck.bd.bbangduck.domain.auth.service.AccountService;
import bbangduck.bd.bbangduck.global.config.properties.JwtSecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 인증에 대한 처리를 JWT Token 을 통해 하기 위해 구현된 Provider <br>
 * 토큰 생성, Request Header 의 Token 정보 조회, 토큰 유효성 검증, JWT Token 을 통한 인증 정보 반환, <br>
 * JWT Token 내의 Email 반환 등의 로직을 담고 있다.
 */
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    private String secretKey = "temp";
    private final JwtSecurityProperties jwtSecurityProperties;
    private final AccountService accountService;

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        this.secretKey = Base64.getEncoder().encodeToString(jwtSecurityProperties.getSecretKey().getBytes());
    }

    // JWT 토큰 생성
    public String createToken(String email, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(email); // JWT payload 에 저장되는 정보단위
        claims.put("roles", roles); // 정보는 key / value 쌍으로 저장된다.
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + jwtSecurityProperties.getTokenValidTime())) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        // TODO: 2021-05-02 Database 를 거치지 않고 인증 Authentication 을 넘겨주도록 처리
        UserDetails userDetails = accountService.loadUserByUsername(this.getEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getEmail(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // Request의 Header에서 token 값을 가져옵니다. "X-AUTH-TOKEN" : "TOKEN값'
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(jwtSecurityProperties.getJwtTokenHeader());
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public JwtSecurityProperties getJwtSecurityProperties() {
        return jwtSecurityProperties;
    }
}
