package bbangduck.bd.bbangduck.domain.auth.dto;

import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialAuthFailResponseAdaptor {

    private String socialId;

    private String email;

    private String nickname;

    private SocialType socialType;

    public static SocialAuthFailResponseAdaptor exchange(SocialUserInfoInterface socialUserInfo) {
        return SocialAuthFailResponseAdaptor.builder()
                .socialId(socialUserInfo.getSocialId())
                .email(socialUserInfo.getEmail())
                .nickname(socialUserInfo.getNickname())
                .socialType(socialUserInfo.getSocialType())
                .build();
    }

    public static SocialAuthFailResponseAdaptor exchangeOnlySocialType(SocialType socialType) {
        return SocialAuthFailResponseAdaptor.builder()
                .socialId(null)
                .email(null)
                .nickname(null)
                .socialType(socialType)
                .build();
    }
}
