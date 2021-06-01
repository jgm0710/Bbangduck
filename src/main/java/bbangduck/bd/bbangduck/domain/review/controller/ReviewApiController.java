package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.controller.dto.DeepReviewResponseDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.DetailReviewResponseDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewResponseDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.SimpleReviewResponseDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.review.service.ReviewLikeService;
import bbangduck.bd.bbangduck.domain.review.service.ReviewService;
import bbangduck.bd.bbangduck.global.common.ResponseDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 작성자 : 정구민 <br><br>
 * <p>
 * 리뷰 자체에 대한 요청 API 를 구현하기 위한 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewApiController {

    private final ReviewService reviewService;

    private final ReviewLikeService reviewLikeService;

    // TODO: 2021-05-29 리뷰 한 건 조회 로직 만들기
    @GetMapping("/{reviewId}")
    public ResponseEntity<ResponseDto<ReviewResponseDto>> getReview(
            @PathVariable Long reviewId,
            @CurrentUser Member currentMember
    ) {
        Review findReview = reviewService.getReview(reviewId);
        boolean existsReviewLike = getExistsReviewLike(reviewId, currentMember);

        ReviewResponseDto reviewResponseDto = convertReview(findReview, currentMember, existsReviewLike);

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.GET_REVIEW_SUCCESS, reviewResponseDto));
    }

    private boolean getExistsReviewLike(Long reviewId, Member currentMember) {
        if (currentMember != null) {
            return reviewLikeService.getExistsReviewLike(currentMember.getId(), reviewId);
        }
        return false;
    }

    private ReviewResponseDto convertReview(Review findReview, Member currentMember, boolean existsReviewLike) {
        switch (findReview.getReviewType()) {
            case SIMPLE:
                return SimpleReviewResponseDto.convert(findReview, currentMember, existsReviewLike);
            case DETAIL:
                return DetailReviewResponseDto.convert(findReview, currentMember, existsReviewLike);
            case DEEP:
                return DeepReviewResponseDto.convert(findReview, currentMember, existsReviewLike);
            default:
                return null;
        }
    }

    // TODO: 2021-05-22 리뷰 수정 기능 구현

    // TODO: 2021-05-22 리뷰 삭제 기능 구현
}
