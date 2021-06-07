package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewResponseDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewUpdateRequestDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.service.ReviewLikeService;
import bbangduck.bd.bbangduck.domain.review.service.ReviewService;
import bbangduck.bd.bbangduck.global.common.ResponseDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static bbangduck.bd.bbangduck.domain.review.controller.ReviewResponseUtils.convertReviewToResponseDto;

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

    // TODO: 2021-05-22 리뷰 삭제 기능 구현

    // TODO: 2021-06-07 리뷰 좋아요 등록 기능 구현

    // TODO: 2021-06-07 리뷰 좋아요 등록 해제 기능 구현
}
