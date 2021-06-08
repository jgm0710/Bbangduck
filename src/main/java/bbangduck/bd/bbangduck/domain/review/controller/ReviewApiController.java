package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewResponseDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewSurveyCreateRequestDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewUpdateRequestDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.exception.AddSurveysToReviewsCreatedByOtherMembersException;
import bbangduck.bd.bbangduck.domain.review.service.ReviewLikeService;
import bbangduck.bd.bbangduck.domain.review.service.ReviewService;
import bbangduck.bd.bbangduck.global.common.ResponseDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static bbangduck.bd.bbangduck.domain.review.controller.ReviewResponseUtils.convertReviewToResponseDto;
import static bbangduck.bd.bbangduck.global.common.ThrowUtils.hasErrorsThrow;

/**
 * 작성자 : 정구민 <br><br>
 * <p>
 * 리뷰 자체에 대한 요청 API 를 구현하기 위한 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews/{reviewId}")
public class ReviewApiController {

    private final ReviewService reviewService;

    private final ReviewLikeService reviewLikeService;

    private final ReviewValidator reviewValidator;

    // TODO: 2021-06-08 리뷰 1건 조회 로직 수정, 리뷰 구분이 바뀜
    @GetMapping
    public ResponseEntity<ResponseDto<ReviewResponseDto>> getReview(
            @PathVariable Long reviewId,
            @CurrentUser Member currentMember
    ) {
        Review findReview = reviewService.getReview(reviewId);
        boolean existsReviewLike = getExistsReviewLike(reviewId, currentMember);

        ReviewResponseDto reviewResponseDto = convertReviewToResponseDto(findReview, currentMember, existsReviewLike);

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.GET_REVIEW_SUCCESS, reviewResponseDto));
    }

    private boolean getExistsReviewLike(Long reviewId, Member currentMember) {
        if (currentMember != null) {
            return reviewLikeService.getExistsReviewLike(currentMember.getId(), reviewId);
        }
        return false;
    }

    // TODO: 2021-05-22 리뷰 수정 기능 구현
    @PutMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity updateReview(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewUpdateRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
//        reviewValidator.validateUpdateReview(requestDto, errors);

//        reviewService.updateReview(currentMember.getId(), requestDto.toServiceDto());

//        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ResponseDto<>(ResponseStatus));
        return null;
    }

    // TODO: 2021-06-07 리뷰에 설문 추가 기능 구현
    // TODO: 2021-06-08 test
    /**
     * 기능 테스트
     * - 204
     *
     * 실패 테스트
     * - 리뷰를 찾을 수 없는 경우 - not found
     * - 리뷰 설문에 등록할 장르를 찾을 수 없는 경우 - not found
     * - validation 검증 - bad request
     * - 다른 회원이 생성한 리뷰에 설문 등록 - forbidden
     * - 리뷰 생성 이후 7일이 지난 경우 - conflict
     * - 인증되지 않았을 경우 - unauthorized
     * - 탈퇴한 회원일 경우  - forbidden
     */
    // TODO: 2021-06-09 장르코드 5개 이상일 경우 validation 에러 발생
    @PostMapping("/surveys")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<Object>> addSurveyToReview(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewSurveyCreateRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        reviewValidator.validateAddSurveyToReview(requestDto, errors);
        hasErrorsThrow(ResponseStatus.ADD_SURVEY_TO_REVIEW_NOT_VALID, errors);

        Review findReview = reviewService.getReview(reviewId);
        Member reviewMember = findReview.getMember();
        if (!reviewMember.getId().equals(currentMember.getId())) {
            throw new AddSurveysToReviewsCreatedByOtherMembersException();
        }

        reviewService.addSurveyToReview(reviewId, requestDto.toServiceDto());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ResponseDto<>(ResponseStatus.ADD_SURVEY_TO_REVIEW_SUCCESS, null));
    }

    // TODO: 2021-06-08 리뷰에 추가한 설문 수정 기능 구현

    // TODO: 2021-05-22 리뷰 삭제 기능 구현

    // TODO: 2021-06-07 리뷰 좋아요 등록 기능 구현

    // TODO: 2021-06-07 리뷰 좋아요 등록 해제 기능 구현
}
