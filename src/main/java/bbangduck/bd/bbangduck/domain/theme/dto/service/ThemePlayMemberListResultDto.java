package bbangduck.bd.bbangduck.domain.theme.dto.service;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * {@link bbangduck.bd.bbangduck.domain.theme.service.ThemeApplicationService#getThemePlayMemberList(Long, ThemeGetPlayMemberListDto)} 에서
 * 회원 플레이 목록 조회에 대한 응답 값을 한 번에 응답하기 위해 만든 Dto
 *
 * @author Gumin Jeong
 * @since 2021-07-22
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemePlayMemberListResultDto {

    private List<Member> members;

    private long themePlayMembersCount;

}
