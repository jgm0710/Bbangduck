package bbangduck.bd.bbangduck.global.security.social.kakao.dto;

import bbangduck.bd.bbangduck.domain.member.model.SocialType;
import bbangduck.bd.bbangduck.global.security.social.common.SocialUserInfoInterface;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfoDto implements SocialUserInfoInterface {
    private String id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

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
        return kakaoAccount.getProfile().getNickname();
    }
}
