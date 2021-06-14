package bbangduck.bd.bbangduck.domain.review.controller.dto.request;

import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewSortCondition;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewSearchDto;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 작성자 : 정구민 <br><br>
 * <p>
 * 테마와 관련된 리뷰 목록 조회 시 조회 조건들을 담을 요청 Dto
 */
@Data
@AllArgsConstructor
public class ThemeReviewSearchRequestDto {

    @Min(value = 1, message = "페이지 번호는 1보다 작을 수 없습니다.")
    private Integer pageNum;

    @Min(value = 1, message = "한 번에 조회할 수 있는 수량은 1 개 보다 작을 수 없습니다.")
    @Max(value = 200, message = "한 번에 조회할 수 있는 수량은 200 개 보다 많을 수 없습니다.")
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
