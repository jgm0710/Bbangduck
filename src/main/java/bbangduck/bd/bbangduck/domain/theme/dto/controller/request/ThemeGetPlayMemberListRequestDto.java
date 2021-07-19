package bbangduck.bd.bbangduck.domain.theme.dto.controller.request;

import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemeGetPlayMemberListDto;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeGetMemberListSortCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 테마를 플레이한 회원 목록 조회 시 요청 Data 를 받을 Dto
 *
 * @author Gumin Jeong
 * @since 2021-07-19
 */
@Data
@Builder
@AllArgsConstructor
public class ThemeGetPlayMemberListRequestDto {

    @Range(min = 1,max = 10, message = "테마 플레이 회원 조회 가능 수량은 1~10 사이 입니다.")
    private Integer amount;

    private ThemeGetMemberListSortCondition sortCondition;

    public ThemeGetPlayMemberListRequestDto() {
        this.amount = 3;
        this.sortCondition = ThemeGetMemberListSortCondition.REVIEW_LIKE_COUNT_DESC;
    }


    public ThemeGetPlayMemberListDto toServiceDto() {
        return ThemeGetPlayMemberListDto.builder()
                .amount(amount)
                .sortCondition(sortCondition)
                .build();
    }
}
