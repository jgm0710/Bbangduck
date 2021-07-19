package bbangduck.bd.bbangduck.domain.theme.dto.controller.response;


import bbangduck.bd.bbangduck.domain.member.dto.controller.response.MemberProfileImageResponseDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 테마를 플레이한 회원에 대한 간단한 정보를 담을 응답 Dto
 *
 * @author Gumin Jeong
 * @since 2021-07-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemePlayMemberSimpleInfoResponseDto {

    private Long memberId;

    private String nickname;

    private MemberProfileImageResponseDto profileImage;

    public static ThemePlayMemberSimpleInfoResponseDto convert(Member member) {
        return ThemePlayMemberSimpleInfoResponseDto.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .profileImage(MemberProfileImageResponseDto.convert(member.getProfileImage()))
                .build();
    }

}
