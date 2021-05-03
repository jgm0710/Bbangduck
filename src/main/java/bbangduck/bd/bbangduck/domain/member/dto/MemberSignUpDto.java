package bbangduck.bd.bbangduck.domain.member.dto;

import bbangduck.bd.bbangduck.domain.member.entity.*;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 가입 요청 시 요청 Body 의 데이터를 담기 위한 Dto
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSignUpDto {

    @Email(message = "Email 형식에 맞게 Email 을 기입해 주세요.")
    @NotBlank(message = "Email 을 입력해 주세요.")
    private String email;

    @NotBlank(message = "Nickname 을 입력해 주세요.")
    private String nickname;

    private String password;

    private SocialType socialType;

    private String socialId;

    @Builder
    public MemberSignUpDto(String email, String nickname, String password, SocialType socialType, String socialId) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.socialType = socialType;
        this.socialId = socialId;
    }

    public Member signUp(int refreshTokenExpiredDate) {
        Member member = Member.builder()
                .email(this.email)
                .password(this.password)
                .profileImage(null)
                .nickname(this.nickname)
                .description(null)
                .reviewCount(0)
                .refreshInfo(RefreshInfo.init(refreshTokenExpiredDate))
                .roles(Set.of(MemberRole.USER))
                .build();

        SocialAccount socialAccount = SocialAccount.builder()
                .socialId(this.socialId)
                .socialType(this.socialType)
                .build();

        member.addSocialAccount(socialAccount);

        return member;
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
