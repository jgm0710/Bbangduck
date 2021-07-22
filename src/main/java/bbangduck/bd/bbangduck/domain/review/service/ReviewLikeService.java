package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewLike;
import bbangduck.bd.bbangduck.domain.review.exception.AddLikeToMyReviewException;
import bbangduck.bd.bbangduck.domain.review.exception.ReviewHasAlreadyBeenLikedException;
import bbangduck.bd.bbangduck.domain.review.exception.ReviewLikeNotFoundException;
import bbangduck.bd.bbangduck.domain.review.exception.ReviewNotFoundException;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewLikeQueryRepository;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewLikeRepository;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 좋아요에 대한 처리 비즈니스 로직을 위해 구현한 Service
 */
@Service
@RequiredArgsConstructor
public class ReviewLikeService {

    private final ReviewLikeRepository reviewLikeRepository;

    private final ReviewLikeQueryRepository reviewLikeQueryRepository;

    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public boolean getExistsReviewLike(Long memberId, Long reviewId) {
        return reviewLikeQueryRepository.findByMemberIdAndReviewId(memberId, reviewId).isPresent();
    }

    @Transactional
    public void addLikeToReview(Member member, Review review) {
        if (getExistsReviewLike(member.getId(), review.getId())) {
            throw new ReviewHasAlreadyBeenLikedException(member.getId(), review.getId());
        }
        if (review.isMyReview(member)) {
            throw new AddLikeToMyReviewException();
        }

        ReviewLike reviewLike = ReviewLike.init(member, review);
        review.increaseLikeCount();

        reviewLikeRepository.save(reviewLike);
    }

    /**
     * 기능 테스트 o
     * - 기존에 등록돼 있던 좋아요가 잘 삭제되는지 좋아요 조회를 통해 확인
     * - 리뷰의 like count 가 잘 감소하는지 확인
     *
     * todo : 실패 테스트 미완
     * 실패 테스트
     * - 리뷰에 좋아요가 등록되어 있지 않은 경우
     * - 회원을 찾을 수 없는 경우
     * - 리뷰를 찾을 수 없는 경우
     */
    @Transactional
    public void removeLikeFromReview(Long memberId, Long reviewId) {
        ReviewLike reviewLike = reviewLikeQueryRepository.findByMemberIdAndReviewId(memberId, reviewId).orElseThrow(() -> new ReviewLikeNotFoundException(memberId, reviewId));
        reviewLikeRepository.delete(reviewLike);
        Review review = reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);
        review.decreaseLikeCount();
    }

}
