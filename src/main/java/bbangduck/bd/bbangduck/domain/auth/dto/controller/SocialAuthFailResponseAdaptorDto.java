package bbangduck.bd.bbangduck.domain.auth.dto.controller;

import bbangduck.bd.bbangduck.domain.auth.dto.service.SocialUserInfoInterface;
import bbangduck.bd.bbangduck.domain.member.entity.enumerate.SocialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialAuthFailResponseAdaptorDto {

    private String socialId;

    private String email;

    private String nickname;

    private SocialType socialType;

    public static SocialAuthFailResponseAdaptorDto exchange(SocialUserInfoInterface socialUserInfo) {
        return SocialAuthFailResponseAdaptorDto.builder()
                .socialId(socialUserInfo.getSocialId())
                .email(socialUserInfo.getEmail())
                .nickname(socialUserInfo.getNickname())
                .socialType(socialUserInfo.getSocialType())
                .build();
    }

    public static SocialAuthFailResponseAdaptorDto exchangeOnlySocialType(SocialType socialType) {
        return SocialAuthFailResponseAdaptorDto.builder()
                .socialId(null)
                .email(null)
                .nickname(null)
                .socialType(socialType)
                .build();
    }
}
