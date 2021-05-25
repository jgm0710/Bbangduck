package bbangduck.bd.bbangduck.domain.auth.service.dto;

import bbangduck.bd.bbangduck.domain.member.entity.enumerate.SocialType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원가입 Service 로직 호출 시 필요한 정보를 담을 Service Dto
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSignUpDto {

    private String email;

    private String nickname;

    private String password;

    private SocialType socialType;

    private String  socialId;

    @Builder
    public MemberSignUpDto(String email, String nickname, String password, SocialType socialType, String  socialId) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.socialType = socialType;
        this.socialId = socialId;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    public SocialType getSocialType() {
        return socialType;
    }

    public String getSocialId() {
        return socialId;
    }
}
