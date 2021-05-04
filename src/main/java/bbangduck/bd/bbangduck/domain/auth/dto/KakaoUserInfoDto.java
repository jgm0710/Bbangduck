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

    @JsonProperty("id")
    private String id;

    @JsonProperty("kakao_account")
    private KakaoAccountDto kakaoAccount;

    @Builder
    public KakaoUserInfoDto(String id, boolean profileNeedsAgreement, String nickname, String thumbnailImageUrl, String profileImageUrl, boolean emailNeedsAgreement, boolean isEmailValid, boolean isEmailVerified, String email) {
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
    public String getSocialId() {
        return id;
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

    @Override
    public String toString() {
        return "KakaoUserInfoDto{" +
                "kakaoUserId='" + id + '\'' +
                ", kakaoAccount=" + kakaoAccount +
                '}';
    }
}
