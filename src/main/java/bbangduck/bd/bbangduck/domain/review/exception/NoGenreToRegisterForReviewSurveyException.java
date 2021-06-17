package bbangduck.bd.bbangduck.domain.review.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.BadRequestException;

/**
 * 작성자 : 정구민 <br>
 * 작성 일자 : 2021-06-11 <br><br>
 *
 * 리뷰 설문 등록, 수정 시 장르 코드를 전혀 기입하지 않는 경우 발생할 예외
 */
public class NoGenreToRegisterForReviewSurveyException extends BadRequestException {
    public NoGenreToRegisterForReviewSurveyException() {
        super(ResponseStatus.NO_GENRE_TO_REGISTER_FOR_REVIEW_SURVEY);
    }
}
