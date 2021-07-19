package bbangduck.bd.bbangduck.domain.theme.dto.controller.response;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.global.common.NullCheckUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 테마를 플레이한 회원 목록 조회 시 응답 Data 를 담을 Dto
 *
 * @author Gumin Jeong
 * @since 2021-07-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemePlayMemberListResponseDto {

    private List<ThemePlayMemberSimpleInfoResponseDto> membersInfo;

    private Integer requestAmount;

    private Long themePlayMembersCount;

    public static ThemePlayMemberListResponseDto convert(List<Member> memberList, Integer requestAmount, Long themePlayMembersCount) {
        return ThemePlayMemberListResponseDto.builder()
                .membersInfo(convertMemberToMembersInfo(memberList))
                .requestAmount(requestAmount)
                .themePlayMembersCount(themePlayMembersCount)
                .build();
    }

    private static List<ThemePlayMemberSimpleInfoResponseDto> convertMemberToMembersInfo(List<Member> memberList) {
        List<ThemePlayMemberSimpleInfoResponseDto> membersInfo = null;
        if (NullCheckUtils.existsList(memberList)) {
            membersInfo = memberList.stream().map(ThemePlayMemberSimpleInfoResponseDto::convert).collect(Collectors.toList());
        }
        return membersInfo;
    }


}
