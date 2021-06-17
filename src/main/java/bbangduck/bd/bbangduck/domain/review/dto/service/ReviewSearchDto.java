package bbangduck.bd.bbangduck.domain.review.dto.service;

import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewSearchType;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewSortCondition;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 목록 조회 시 사용할 Service Dto
 */
@NoArgsConstructor
public class ReviewSearchDto {

    private CriteriaDto criteria;

    private ReviewSearchType searchType;

    private ReviewSortCondition sortCondition;

    @Builder
    public ReviewSearchDto(CriteriaDto criteria, ReviewSearchType searchType, ReviewSortCondition sortCondition) {
        this.criteria = criteria;
        this.searchType = searchType;
        this.sortCondition = sortCondition;
    }

    public int getOffset() {
        return criteria.getOffset();
    }

    public int getAmount() {
        return criteria.getAmount();
    }

    public ReviewSortCondition getSortCondition() {
        return sortCondition;
    }

    public ReviewSearchType getSearchType() {
        return searchType;
    }

    public Integer getPrevPageNum() {
        int prevPageNum = criteria.getPageNum() - 1;

        if (prevPageNum > 0) {
            return prevPageNum;
        } else {
            return null;
        }
    }

    public int getNextPageNum() {
        return criteria.getPageNum() + 1;
    }

    public int getPageNum() {
        return this.criteria.getPageNum();
    }
}
