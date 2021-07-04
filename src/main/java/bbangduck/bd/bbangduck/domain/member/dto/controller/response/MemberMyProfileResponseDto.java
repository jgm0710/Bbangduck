package bbangduck.bd.bbangduck.domain.member.dto.controller.response;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberPlayInclination;
import bbangduck.bd.bbangduck.domain.member.entity.SocialAccount;
import bbangduck.bd.bbangduck.domain.review.dto.entity.ReviewRecodesCountsDto;
import bbangduck.bd.bbangduck.global.common.NullCheckUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 자신의 프로필을 조회하는 경우의 응답 Body Data 를 담을 Dto
 */
@Getter
@Setter
public class MemberMyProfileResponseDto extends MemberProfileResponseDto{

    private String email;

    private List<SocialAccountResponseDto> socialAccounts;

    private LocalDateTime registerTimes;

    private LocalDateTime updateTimes;

    protected MemberMyProfileResponseDto(Member member, ReviewRecodesCountsDto reviewRecodesCountsDto, List<MemberPlayInclination> memberPlayInclinations) {
        super(member, reviewRecodesCountsDto, memberPlayInclinations);
        this.email = member.getEmail();
        this.socialAccounts = convertSocialAccountsToResponseDtos(member.getSocialAccounts());
        this.registerTimes = member.getRegisterTimes();
        this.updateTimes = member.getUpdateTimes();
        super.myProfile = true;
    }

    private List<SocialAccountResponseDto> convertSocialAccountsToResponseDtos(List<SocialAccount> socialAccounts) {
        if (NullCheckUtils.existsList(socialAccounts)) {
            return socialAccounts.stream()
                        .map(SocialAccountResponseDto::convert).collect(Collectors.toList());
        }
        return null;
    }

    public static MemberMyProfileResponseDto convert(Member member, ReviewRecodesCountsDto reviewRecodesCountsDto,List<MemberPlayInclination> memberPlayInclinations) {
        return new MemberMyProfileResponseDto(member, reviewRecodesCountsDto, memberPlayInclinations);
    }
}
