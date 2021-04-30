package bbangduck.bd.bbangduck.global.security.social.common.dto;

import bbangduck.bd.bbangduck.domain.member.model.SocialType;
import bbangduck.bd.bbangduck.global.security.social.common.SocialUserInfoInterface;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialUserInfoDto {

    private String socialId;

    private String email;

    private String nickname;

    private SocialType socialType;

    public static SocialUserInfoDto createSocialRegisterDto(SocialUserInfoInterface socialUserInfo) {
        return SocialUserInfoDto.builder()
                .socialId(socialUserInfo.getId())
                .email(socialUserInfo.getEmail())
                .nickname(socialUserInfo.getNickname())
                .socialType(socialUserInfo.getSocialType())
                .build();
    }
}
