package bbangduck.bd.bbangduck.domain.auth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 작성자 : 정구민 <br><br>
 *
 * API 요청 시 인증이 완료된 회원의 Authentication Principal 내의 회원 정보를 편하게 가져다 쓰기 위한 Annotation
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : member")
public @interface CurrentUser {
}