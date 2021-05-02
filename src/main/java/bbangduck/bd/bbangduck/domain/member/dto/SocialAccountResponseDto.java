package bbangduck.bd.bbangduck.domain.member.dto;

import bbangduck.bd.bbangduck.domain.member.entity.SocialAccount;
import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import lombok.*;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원의 SocialType 을 응답하기 위한 Dto
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialAccountResponseDto {

    private Long socialAccountId;

    private String socialId;

    private SocialType socialType;

    @Builder
    public SocialAccountResponseDto(Long socialAccountId, String socialId, SocialType socialType) {
        this.socialAccountId = socialAccountId;
        this.socialId = socialId;
        this.socialType = socialType;
    }

    public static SocialAccountResponseDto convert(SocialAccount socialAccount) {
        if (socialAccount == null) {
            return null;
        }

        return SocialAccountResponseDto.builder()
                .socialAccountId(socialAccount.getId())
                .socialId(socialAccount.getSocialId())
                .socialType(socialAccount.getSocialType())
                .build();
    }
}
