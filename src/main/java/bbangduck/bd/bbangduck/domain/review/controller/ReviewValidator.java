package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewCreateRequestDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewImageRequestDto;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

        switch (reviewType) {
            case SIMPLE:
                if (!requestDto.isSimpleReview()) {
                    errors.reject("NotSimpleReview", "간단 리뷰 작성 시 기입하지 않아야 하는 사항이 기입되었습니다.");
                }
                hasErrorsThrow(ResponseStatus.CREATE_SIMPLE_REVIEW_NOT_VALID, errors);
                break;

            case DETAIL:
                if (requestDto.isDetailReview()) {
                    validateDetailReview(requestDto, errors);
                } else {
                    errors.reject("NotDetailReview", "상세 리뷰 작성 시 기입하지 않아야 하는 사항이 기입되었습니다.");
                }
                hasErrorsThrow(ResponseStatus.CREATE_DETAIL_REVIEW_NOT_VALID, errors);
                break;

            case DEEP:
                validateDeepReview(requestDto, errors);
                hasErrorsThrow(ResponseStatus.CREATE_DEEP_REVIEW_NOT_VALID, errors);
                break;

            default:
                break;
        }
    }

    public boolean genreCodeExists(String genreCode) {
        return genreCode != null && !genreCode.isBlank();
    }

    private void validateDeepReview(ReviewCreateRequestDto requestDto, Errors errors) {
        validateDetailReview(requestDto, errors);
        // TODO: 2021-05-30 체감 장르에 대한 부분 논의 후 반영
        if (!requestDto.genreCodesExists()) {
            errors.rejectValue("genreCodes", "NotEmpty", "상세 및 설문 리뷰에 등록될 체감 테마 장르 목록을 기입해 주세요.");
        }else{
            List<String> genreCodes = requestDto.getGenreCodes();
            for (int i = 0; i < genreCodes.size(); i++) {
                if (!genreCodeExists(genreCodes.get(i))) {
                    errors.rejectValue("genreCodes[" + i + "]", "NotBlank", "상세 및 설문 리뷰에 등록될 체감 테마 목록 중 [" + i + "] 번 째 테마 코드가 기입되지 않았습니다.");
                }
            }
        }


        if (requestDto.perceivedDifficultyIsNull()) {
            errors.rejectValue("perceivedDifficulty", "NotBlank", "상세 및 설문 리뷰에 등록될 체감 난이도를 기입해 주세요.");
        }

        if (requestDto.perceivedHorrorGradeIsNull()) {
            errors.rejectValue("perceivedHorrorGrade", "NotBlank", "상세 및 설문 리뷰에 등록될 체감 공포도를 기입해 주세요.");
        }

        if (requestDto.perceivedActivityIsNull()) {
            errors.rejectValue("perceivedActivity", "NotBlank", "상세 및 설문 리뷰에 등록될 체감 활동성을 기입해 주세요.");
        }

        if (requestDto.scenarioSatisfactionIsNull()) {
            errors.rejectValue("scenarioSatisfaction", "NotBlank", "상세 및 설문 리뷰에 등록될 시나리오 만족도를 기입해 주세요.");
        }

        if (requestDto.interiorSatisfactionIsNull()) {
            errors.rejectValue("interiorSatisfaction", "NotBlank", "상세 및 설문 리뷰에 등록될 인테리어 만족도를 기입해 주세요.");
        }

        if (requestDto.problemConfigurationSatisfactionIsNull()) {
            errors.rejectValue("problemConfigurationSatisfaction", "NotBlank", "상세 및 설문 리뷰에 등록될 문제 구성도를 기입해 주세요.");
        }
    }

    private void validateDetailReview(ReviewCreateRequestDto requestDto, Errors errors) {
        if (requestDto.commentNotExists()) {
            errors.rejectValue("comment", "NotBlank", "상세 리뷰에 등록될 내용을 기입해 주세요.");
        }

        if (requestDto.reviewImagesExists()) {
            validateReviewImages(requestDto.getReviewImages(), errors);
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
