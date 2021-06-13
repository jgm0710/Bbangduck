package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.review.controller.dto.*;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.config.properties.ReviewProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalTime;
import java.util.List;

import static bbangduck.bd.bbangduck.global.common.NullCheckUtils.*;
import static bbangduck.bd.bbangduck.global.common.ThrowUtils.hasErrorsThrow;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 생성, 수정 등에서 클라이언트로부터 받은 요청 데이터에 대해 기본 Validation annotation 보다 복잡한 검증이 필요한 경우
 * 사용될 Custom Validator
 */
@Component
@RequiredArgsConstructor
public class ReviewValidator {

    private final ReviewProperties reviewProperties;

    public void validateCreateView(ReviewCreateRequestDto requestDto, Errors errors) {
        hasErrorsThrow(ResponseStatus.CREATE_REVIEW_NOT_VALID, errors);

        checkClearTime(requestDto.getClearYN(), requestDto.getClearTime(), errors);
        checkPlayTogetherFriendsCount(requestDto.getFriendIds(), reviewProperties.getPlayTogetherFriendsCountLimit(), errors);
        ReviewType reviewType = requestDto.getReviewType();
        validateAccordingToReviewType(reviewType, requestDto.getReviewImages(), requestDto.getComment(), errors);

        hasErrorsThrow(ResponseStatus.CREATE_REVIEW_NOT_VALID, errors);
    }

    public void validateUpdateReview(ReviewUpdateRequestDto requestDto, Errors errors) {
        hasErrorsThrow(ResponseStatus.UPDATE_REVIEW_NOT_VALID, errors);

        checkClearTime(requestDto.getClearYN(), requestDto.getClearTime(), errors);
        checkPlayTogetherFriendsCount(requestDto.getFriendIds(), reviewProperties.getPerceivedThemeGenresCountLimit(), errors);
        validateAccordingToReviewType(requestDto.getReviewType(), requestDto.getReviewImages(), requestDto.getComment(), errors);

        hasErrorsThrow(ResponseStatus.UPDATE_REVIEW_NOT_VALID, errors);
    }

    public void validateAddDetailToReview(ReviewDetailCreateRequestDto requestDto, Errors errors) {
        validateReviewImages(requestDto.getReviewImages(), errors);

        hasErrorsThrow(ResponseStatus.ADD_SURVEY_TO_REVIEW_NOT_VALID, errors);
    }

    public void validateUpdateDetailFromReview(ReviewDetailUpdateRequestDto requestDto, Errors errors) {
        validateReviewImages(requestDto.getReviewImages(), errors);

        hasErrorsThrow(ResponseStatus.UPDATE_DETAIL_FROM_REVIEW_NOT_VALID, errors);
    }

    public void validateAddSurveyToReview(ReviewSurveyCreateRequestDto requestDto, Errors errors) {
        hasErrorsThrow(ResponseStatus.ADD_SURVEY_TO_REVIEW_NOT_VALID, errors);

        List<String> genreCodes = requestDto.getGenreCodes();
        int perceivedThemeGenresCountLimit = reviewProperties.getPerceivedThemeGenresCountLimit();
        checkPerceivedThemeGenresCount(genreCodes, perceivedThemeGenresCountLimit, errors);

        hasErrorsThrow(ResponseStatus.ADD_SURVEY_TO_REVIEW_NOT_VALID, errors);
    }

    public void validateUpdateSurveyFromReview(ReviewSurveyUpdateRequestDto requestDto, Errors errors) {
        hasErrorsThrow(ResponseStatus.UPDATE_SURVEY_FROM_REVIEW_NOT_VALID, errors);

        List<String> genreCodes = requestDto.getGenreCodes();
        int perceivedThemeGenresCountLimit = reviewProperties.getPerceivedThemeGenresCountLimit();
        checkPerceivedThemeGenresCount(genreCodes, perceivedThemeGenresCountLimit, errors);

        hasErrorsThrow(ResponseStatus.UPDATE_SURVEY_FROM_REVIEW_NOT_VALID, errors);
    }




    private void checkClearTime(Boolean clearYN, LocalTime clearTime, Errors errors) {
        if (clearYN) {
            if (!isNotNull(clearTime)) {
                errors.rejectValue("clearTime", "NotNull", "테마를 클리어 했을 경우 클리어 시간을 기입해 주세요.");
            }
        } else {
            if (isNotNull(clearTime)) {
                errors.rejectValue("clearTime", "NotNeeded", "테마를 클리어하지 않았을 경우 클리어 시간을 필요로 하지 않습니다.");
            }
        }
    }

    private void validateAccordingToReviewType(ReviewType reviewType, List<ReviewImageRequestDto> reviewImages, String comment, Errors errors) {
        switch (reviewType) {
            case SIMPLE:
                simpleReviewValidate(errors, reviewImages, comment);
                break;
            case DETAIL:
                detailReviewValidate(errors, reviewImages, comment);
                break;
            default:
                break;
        }
    }

    private void checkPerceivedThemeGenresCount(List<String> genreCodes, int perceivedThemeGenresCountLimit, Errors errors) {
        if (!existsList(genreCodes)) {
            return;
        }

        if (genreCodes.size() > perceivedThemeGenresCountLimit) {
            errors.rejectValue("genreCodes", "ExceededDataCount", "리뷰 설문에 등록할 체감 장르의 개수가 제한된 개수보다 많습니다. 체감 테마 장르 등록 가능 개수 : " + perceivedThemeGenresCountLimit);
        }
    }

    private void detailReviewValidate(Errors errors, List<ReviewImageRequestDto> reviewImages, String comment) {
        if (!existsString(comment)) {
            errors.rejectValue("comment","NotBlank", "상세 리뷰 등록에 필요한 코멘트를 기입해 주세요.");
        }
        if (existsList(reviewImages)) {
            validateReviewImages(reviewImages, errors);
        }
    }

    private void simpleReviewValidate(Errors errors, List<ReviewImageRequestDto> reviewImages, String comment) {
        if (existsString(comment)) {
            errors.rejectValue("comment","NotNeeded", "간단 리뷰는 코멘트를 필요로 하지 않습니다.");
        }
        if (existsList(reviewImages)) {
            errors.rejectValue("reviewImages","NotNeeded","간단 리뷰는 이미지를 필요로 하지 않습니다.");
        }
    }

    private void checkPlayTogetherFriendsCount(List<Long> friendIds,int playTogetherFriendsCountLimit, Errors errors) {
        if (!existsList(friendIds)) {
            return;
        }

        if (friendIds.size()>playTogetherFriendsCountLimit) {
            errors.rejectValue("friendIds", "ExceededDataCount", "함께 플레이한 친구 ID 수가 제한된 개수보다 많습니다. 함께한 친구 추가 가능 개수 : " + playTogetherFriendsCountLimit);
        }
    }

    private void validateReviewImages(List<ReviewImageRequestDto> reviewImageRequestDtos, Errors errors) {
        for (int i = 0; i < reviewImageRequestDtos.size(); i++) {
            ReviewImageRequestDto reviewImageRequestDto = reviewImageRequestDtos.get(i);

            if (!reviewImageRequestDto.fileStorageIdExists()) {
                errors.rejectValue("reviewImages[" + i + "].fileStorageId", "NotNull",
                        "리뷰에 등록될 이미지 파일 중 [" + i + "] 번 파일의 파일 저장소 ID 를 기입하지 않았습니다.");
            }
            if (!reviewImageRequestDto.fileNameExists()) {
                errors.rejectValue("reviewImages[" + i + "].fileName", "NotBlank", "리뷰에 등록될 이미지 파일 중 [" + i + "] 번 파일의 파일 이름을 기입하지 않았습니다.");
            }
        }
    }

}

