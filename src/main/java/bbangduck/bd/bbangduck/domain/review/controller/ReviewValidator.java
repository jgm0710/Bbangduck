package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewCreateRequestDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewImageRequestDto;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.List;

import static bbangduck.bd.bbangduck.global.common.NullCheckUtils.existsList;
import static bbangduck.bd.bbangduck.global.common.NullCheckUtils.existsString;
import static bbangduck.bd.bbangduck.global.common.ThrowUtils.hasErrorsThrow;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 생성, 수정 등에서 클라이언트로부터 받은 요청 데이터에 대해 기본 Validation annotation 보다 복잡한 검증이 필요한 경우
 * 사용될 Custom Validator
 */
@Component
public class ReviewValidator {

    public void validateCreateView(ReviewCreateRequestDto requestDto, Errors errors) {
        hasErrorsThrow(ResponseStatus.CREATE_REVIEW_NOT_VALID, errors);

        ReviewType reviewType = requestDto.getReviewType();

        List<ReviewImageRequestDto> reviewImages = requestDto.getReviewImages();
        String comment = requestDto.getComment();

        switch (reviewType) {
            case SIMPLE:
                if (existsString(comment)) {
                    errors.rejectValue("comment","NotNeeded", "간단 리뷰 생성 시 코멘트는 필요하지 않습니다.");
                }
                if (existsList(reviewImages)) {
                    errors.rejectValue("reviewImages","NotNeeded","간단 리뷰 생성 시 이미지는 필요하지 않습니다.");
                }
                hasErrorsThrow(ResponseStatus.CREATE_SIMPLE_REVIEW_NOT_VALID, errors);
                break;
            case DETAIL:
                if (!existsString(comment)) {
                    errors.rejectValue("comment","NotBlank", "상세 리뷰 생성 시 필요한 코멘트를 기입해 주세요.");
                }
                if (existsList(reviewImages)) {
                    validateReviewImages(reviewImages, errors);
                }
                hasErrorsThrow(ResponseStatus.CREATE_DETAIL_REVIEW_NOT_VALID, errors);
                break;
            default:
                break;
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

