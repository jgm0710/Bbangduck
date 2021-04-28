package bbangduck.bd.bbangduck.security.kakao.dto;

import bbangduck.bd.bbangduck.member.social.SocialUserInfoInterface;
import bbangduck.bd.bbangduck.member.social.SocialType;
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
public class KakaoUserInfoInterfaceDto implements SocialUserInfoInterface {
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
