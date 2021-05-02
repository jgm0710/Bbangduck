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
