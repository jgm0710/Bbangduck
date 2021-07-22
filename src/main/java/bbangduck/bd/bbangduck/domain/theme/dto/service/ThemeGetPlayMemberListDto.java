package bbangduck.bd.bbangduck.domain.theme.dto.service;

import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeGetMemberListSortCondition;
import bbangduck.bd.bbangduck.global.common.util.PagingQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 테마를 플레이한 회원 목록 조회 시 필요한 Data 를 담을 Service Dto
 *
 * @author Gumin Jeong
 * @since 2021-07-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemeGetPlayMemberListDto implements PagingQuery {

    private long pageNum;

    private int amount;

    private ThemeGetMemberListSortCondition sortCondition;

}
