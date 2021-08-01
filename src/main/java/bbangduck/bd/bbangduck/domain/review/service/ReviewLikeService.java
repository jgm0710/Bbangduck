package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewLike;
import bbangduck.bd.bbangduck.domain.review.exception.AddLikeToMyReviewException;
import bbangduck.bd.bbangduck.domain.review.exception.ReviewHasAlreadyBeenLikedException;
import bbangduck.bd.bbangduck.domain.review.exception.ReviewLikeNotFoundException;
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
    public boolean isMemberLikeToReview(Long memberId, Long reviewId) {
        return reviewLikeQueryRepository.findByMemberIdAndReviewId(memberId, reviewId).isPresent();
    }

    @Transactional
    public void addLikeToReview(Member member, Review review) {
        if (isMemberLikeToReview(member.getId(), review.getId())) {
            throw new ReviewHasAlreadyBeenLikedException(member.getId(), review.getId());
        }
        if (review.isMyReview(member)) {
            throw new AddLikeToMyReviewException();
        }

        ReviewLike reviewLike = ReviewLike.init(member, review);
        review.increaseLikeCount();

        reviewLikeRepository.save(reviewLike);
    }

    public ReviewLike getReviewLike(Long memberId, Long reviewId) {
        return reviewLikeQueryRepository.findByMemberIdAndReviewId(memberId, reviewId).orElseThrow(() -> new ReviewLikeNotFoundException(memberId, reviewId));
    }


    @Transactional
    public void removeReviewLike(ReviewLike reviewLike) {
        Review review = reviewLike.getReview();
        review.decreaseLikeCount();
        reviewLikeRepository.delete(reviewLike);
    }

}
