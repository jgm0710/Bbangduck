package bbangduck.bd.bbangduck.domain.review.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.BadRequestException;

/**
 * 작성자 : 정구민 <br>
 * 작성 일자 : 2021-06-14 <br><br>
 *
 * 리뷰에 등록된 리뷰 상세 수정 시 리뷰 상세가 등록되어 있지 않은 리뷰일 경우 발생할 예외
 */
// TODO: 2021-06-14 리뷰 상세 수정 기능 없어질 경우 삭제
public class ReviewHasNotDetailException extends BadRequestException {
    public ReviewHasNotDetailException() {
        super(ResponseStatus.REVIEW_HAS_NOT_DETAIL);
    }
}
