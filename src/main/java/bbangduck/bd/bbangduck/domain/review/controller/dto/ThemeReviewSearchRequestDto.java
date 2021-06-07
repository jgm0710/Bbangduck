package bbangduck.bd.bbangduck.domain.review.controller.dto;

import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewSortCondition;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewSearchDto;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 작성자 : 정구민 <br><br>
 * <p>
 * 테마와 관련된 리뷰 목록 조회 시 조회 조건들을 담을 요청 Dto
 */
@Data
@AllArgsConstructor
public class ThemeReviewSearchRequestDto {

    private Integer pageNum;

    private Integer amount;

    private ReviewSortCondition sortCondition;

    public ThemeReviewSearchRequestDto() {
        this.pageNum = 1;
        this.amount = 20;
        this.sortCondition = ReviewSortCondition.LATEST;
    }

    @JsonIgnore
    public CriteriaDto getCriteriaDto() {
        return new CriteriaDto(pageNum, amount);
    }

    public ReviewSearchDto toServiceDto() {
        return ReviewSearchDto.builder()
                .criteria(getCriteriaDto())
                .sortCondition(sortCondition)
                .build();
    }

}
