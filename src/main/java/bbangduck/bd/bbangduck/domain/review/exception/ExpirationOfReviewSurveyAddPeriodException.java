package bbangduck.bd.bbangduck.domain.review.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.ConflictException;

import java.time.LocalDateTime;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰에 설문 추가 시 설문 추가 가능 날짜가 만료된 경우 발생할 예외
 */
public class ExpirationOfReviewSurveyAddPeriodException extends ConflictException {
    public ExpirationOfReviewSurveyAddPeriodException(LocalDateTime reviewRegisterTimes, LocalDateTime periodForAddingSurveysDateTime) {
        super(ResponseStatus.EXPIRATION_OF_REVIEW_SURVEY_ADD_PERIOD_EXCEPTION, ResponseStatus.EXPIRATION_OF_REVIEW_SURVEY_ADD_PERIOD_EXCEPTION.getMessage() + "ReviewRegisterTimes : " + reviewRegisterTimes + ", PeriodForAddingSurveyDateTime : " + periodForAddingSurveysDateTime);
    }
}
