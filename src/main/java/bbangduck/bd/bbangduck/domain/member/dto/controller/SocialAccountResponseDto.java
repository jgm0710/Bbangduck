package bbangduck.bd.bbangduck.domain.member.dto.controller;

import bbangduck.bd.bbangduck.domain.member.entity.SocialAccount;
import bbangduck.bd.bbangduck.domain.member.enumerate.SocialType;
import lombok.*;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원의 SocialType 을 응답하기 위한 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialAccountResponseDto {

    private String socialId;

    private SocialType socialType;

    public static SocialAccountResponseDto convert(SocialAccount socialAccount) {
        if (socialAccount == null) {
            return null;
        }

        return SocialAccountResponseDto.builder()
                .socialId(socialAccount.getSocialId())
                .socialType(socialAccount.getSocialType())
                .build();
    }
}
