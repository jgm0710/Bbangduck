package bbangduck.bd.bbangduck.domain.auth.service.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 일반 로그인 시 서비스 로직에서 필요한 Email, Password 를 담을 Dto
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSignInDto {

    private String email;

    private String password;

    @Builder
    public MemberSignInDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
