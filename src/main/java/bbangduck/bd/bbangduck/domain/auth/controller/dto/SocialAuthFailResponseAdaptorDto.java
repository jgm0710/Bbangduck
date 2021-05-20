package bbangduck.bd.bbangduck.domain.auth.controller.dto;

import bbangduck.bd.bbangduck.domain.auth.service.dto.SocialUserInfoInterface;
import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

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
