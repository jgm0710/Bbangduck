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

// TODO: 2021-05-23 주석 달기
@Component
public class ReviewValidator {

    public void validateCreateView(ReviewCreateRequestDto requestDto, Errors errors) {
        ReviewType reviewType = requestDto.getReviewType();

        List<ReviewImageRequestDto> reviewImages = requestDto.getReviewImages();

        switch (reviewType) {
            case SIMPLE:
                if (!requestDto.isSimpleReview()) {
                    errors.reject("NotSimpleReview", "간단 리뷰 작성 시 기입하지 않아야 하는 사항이 기입되었습니다.");
                }
                break;

            case DETAIL:
                if (requestDto.isDetailReview()) {
                    validateDetailReview(requestDto, errors);
                } else {
                    errors.reject("NotDetailReview", "상세 리뷰 작성 시 기입하지 않아야 하는 사항이 기입되었습니다.");
                }
                break;

            case DEEP:
                validateDeepReview(requestDto, errors);
                break;

            default:
                break;
        }

        hasErrorsThrow(ResponseStatus.CREATE_REVIEW_NOT_VALID, errors);
    }

    private void validateDeepReview(ReviewCreateRequestDto requestDto, Errors errors) {
        validateDetailReview(requestDto, errors);
        if (requestDto.perceivedDifficultyIsNull()) {
            errors.rejectValue("perceivedDifficulty","NotBlank", "상세 및 설문 리뷰에 등록될 체감 난이도를 기입해 주세요.");
        }

        if (requestDto.perceivedHorrorGradeIsNull()) {
            errors.rejectValue("perceivedHorrorGrade","NotBlank", "상세 및 설문 리뷰에 등록될 체감 공포도를 기입해 주세요.");
        }

        if (requestDto.perceivedActivityIsNull()) {
            errors.rejectValue("perceivedActivity","NotBlank", "상세 및 설문 리뷰에 등록될 체감 활동성을 기입해 주세요.");
        }

        if (requestDto.scenarioSatisfactionIsNull()) {
            errors.rejectValue("scenarioSatisfaction","NotBlank", "상세 및 설문 리뷰에 등록될 시나리오 만족도를 기입해 주세요.");
        }

        if (requestDto.interiorSatisfactionIsNull()) {
            errors.rejectValue("interiorSatisfaction","NotBlank", "상세 및 설문 리뷰에 등록될 인테리어 만족도를 기입해 주세요.");
        }

        if (requestDto.problemConfigurationSatisfactionIsNull()) {
            errors.rejectValue("problemConfigurationSatisfaction","NotBlank", "상세 및 설문 리뷰에 등록될 문제 구성도를 기입해 주세요.");
        }
    }

    private void validateDetailReview(ReviewCreateRequestDto requestDto, Errors errors) {
        if (requestDto.commentNotExists()) {
            errors.rejectValue("comment", "NotBlank", "상세 리뷰에 등록될 내용을 기입해 주세요.");
        }
        validateReviewImages(requestDto.getReviewImages(), errors);
    }

    public boolean reviewImagesExists(List<ReviewImageRequestDto> reviewImageRequestDtos) {
        return reviewImageRequestDtos != null && !reviewImageRequestDtos.isEmpty();
    }

    private void validateReviewImages(List<ReviewImageRequestDto> reviewImageRequestDtos, Errors errors) {
        if (reviewImagesExists(reviewImageRequestDtos)) {
            AtomicInteger index = new AtomicInteger();
            reviewImageRequestDtos.forEach(reviewImageRequestDto -> {
                Long fileStorageId = reviewImageRequestDto.getFileStorageId();
                String fileName = reviewImageRequestDto.getFileName();
                int i = index.getAndIncrement();

                if (!reviewImageRequestDto.fileStorageIdExists()) {
                    errors.rejectValue("fileStorageId", "NotNull",
                            "리뷰에 등록될 이미지 파일 중 [" + i + "] 번 파일의 파일 저장소 ID 를 기입하지 않았습니다.");
                }
                if (!reviewImageRequestDto.fileNameExists()) {
                    errors.rejectValue("fileName", "NotBlank", "리뷰에 등록될 이미지 파일 중 [" + i + "] 번 파일의 파일 이름을 기입하지 않았습니다.");
                }
            });
        }
    }
}
