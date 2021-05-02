package bbangduck.bd.bbangduck.domain.auth.dto;

import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfoDto implements SocialUserInfoInterface {
    private String id;

    @JsonProperty("kakao_account")
    private KakaoAccountDto kakaoAccount;

    @Builder
    public KakaoUserInfoDto(String id, String profileNeedsAgreement, String nickname, String thumbnailImageUrl, String profileImageUrl, boolean emailNeedsAgreement, boolean isEmailValid, boolean isEmailVerified, String email) {
        this.id = id;
        this.kakaoAccount = KakaoAccountDto.builder()
                .profileNeedsAgreement(profileNeedsAgreement)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .thumbnailImageUrl(thumbnailImageUrl)
                .emailNeedsAgreement(emailNeedsAgreement)
                .isEmailValid(isEmailValid)
                .isEmailVerified(isEmailVerified)
                .email(email)
                .build();
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public SocialType getSocialType() {
        return SocialType.KAKAO;
    }

    @Override
    public String getEmail() {
        return kakaoAccount.getEmail();
    }

    @Override
    public String getNickname() {
        return kakaoAccount.getNickname();
    }
}
