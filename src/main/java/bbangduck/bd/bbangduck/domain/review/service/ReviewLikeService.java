package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewLike;
import bbangduck.bd.bbangduck.domain.review.exception.AddLikeToMyReviewException;
import bbangduck.bd.bbangduck.domain.review.exception.ExistsReviewLikeException;
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
@Transactional(readOnly = true)
public class ReviewLikeService {

    private final ReviewLikeRepository reviewLikeRepository;

    private final ReviewLikeQueryRepository reviewLikeQueryRepository;

    private final ReviewRepository reviewRepository;

    private final MemberRepository memberRepository;

    public boolean getExistsReviewLike(Long memberId, Long reviewId) {
        return reviewLikeQueryRepository.findByMemberAndReview(memberId, reviewId).isPresent();
    }

    // TODO: 2021-06-01 test
    // TODO: 2021-06-12 삭제된 리뷰일 경우에 대한 처리 추가
    @Transactional
    public Long addLikeToReview(Long memberId, Long reviewId) {
        if (getExistsReviewLike(memberId, reviewId)) {
            throw new ExistsReviewLikeException(memberId, reviewId);
        }

        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Review review = reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);

        if (review.isMyReview(member)) {
            throw new AddLikeToMyReviewException();
        }

        ReviewLike reviewLike = ReviewLike.builder()
                .member(member)
                .review(review)
                .build();
        ReviewLike savedReviewLike = reviewLikeRepository.save(reviewLike);

        review.increaseLikeCount();

        return savedReviewLike.getId();
    }

    // TODO: 2021-06-01 test
    @Transactional
    public void removeLikeFromReview(Long memberId, Long reviewId) {
        ReviewLike reviewLike = reviewLikeQueryRepository.findByMemberAndReview(memberId, reviewId).orElseThrow(() -> new ReviewLikeNotFoundException(memberId, reviewId));
        reviewLikeRepository.delete(reviewLike);
        Review review = reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);
        review.decreaseLikeCount();
    }

}
