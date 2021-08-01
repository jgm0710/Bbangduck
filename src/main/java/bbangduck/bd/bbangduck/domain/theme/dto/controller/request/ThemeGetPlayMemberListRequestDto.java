package bbangduck.bd.bbangduck.domain.theme.dto.controller.request;

import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemeGetPlayMemberListDto;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeGetMemberListSortCondition;
import bbangduck.bd.bbangduck.global.common.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

/**
 * 테마를 플레이한 회원 목록 조회 시 요청 Data 를 받을 Dto
 *
 * @author Gumin Jeong
 * @since 2021-07-19
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemeGetPlayMemberListRequestDto implements PageRequest {

    @Min(value = 1, message = "1 페이지 이하는 조회할 수 없습니다.")
    @Builder.Default
    private Long pageNum =1L;

    @Range(min = 1,max = 10, message = "테마 플레이 회원 조회 가능 수량은 1~10 사이 입니다.")
    @Builder.Default
    private Integer amount=3;

    @Getter
    @Builder.Default
    private ThemeGetMemberListSortCondition sortCondition = ThemeGetMemberListSortCondition.REVIEW_LIKE_COUNT_DESC;

    public ThemeGetPlayMemberListDto toServiceDto() {
        return ThemeGetPlayMemberListDto.builder()
                .pageNum(pageNum)
                .amount(amount)
                .sortCondition(sortCondition)
                .build();
    }


    @Override
    public long getPageNum() {
        return pageNum;
    }

    @Override
    public int getAmount() {
        return amount;
    }
}
