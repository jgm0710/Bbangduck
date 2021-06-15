package bbangduck.bd.bbangduck.domain.review.dto.controller.request;

import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewSearchDto;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewSearchType;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

// TODO: 2021-06-15 주석
@Data
@Builder
@AllArgsConstructor
public class MemberReviewSearchRequestDto {

    @NotNull(message = "검색 타입을 입력해 주세요.")
    private ReviewSearchType searchType;

    @Min(value = 1, message = "페이지 번호는 1보다 작을 수 없습니다.")
    private Integer pageNum;

    @Min(value = 1, message = "한 번에 조회할 수 있는 수량은 1 개 보다 작을 수 없습니다.")
    @Max(value = 200, message = "한 번에 조회할 수 있는 수량은 200 개 보다 많을 수 없습니다.")
    private Integer amount;

    public MemberReviewSearchRequestDto() {
        this.searchType = ReviewSearchType.TOTAL;
        this.pageNum = 1;
        this.pageNum = 20;
    }

    @JsonIgnore
    public CriteriaDto getCriteriaDto() {
        return new CriteriaDto(pageNum, amount);
    }

    public ReviewSearchDto toServiceDto() {
        return ReviewSearchDto.builder()
                .criteria(getCriteriaDto())
                .searchType(searchType)
                .sortCondition(null)
                .build();
    }
}
