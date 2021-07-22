package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewLike;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewLikeRepository;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReviewLikeServiceIntegrationTest extends BaseTest {

    @Autowired
    ReviewLikeService reviewLikeService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    ReviewLikeRepository reviewLikeRepository;

    @AfterEach
    void tearDown() {
        reviewLikeRepository.deleteAll();
        reviewRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("리뷰에 좋아요 추가")
    public void addLikeToReview() {
        //given
        Member member1 = Member.builder().build();
        Member member2 = Member.builder().build();

        Review review = Review.builder()
                .member(member2)
                .likeCount(0)
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);
        Review savedReview = reviewRepository.save(review);

        //when
        reviewLikeService.addLikeToReview(member1, savedReview);

        //then
        assertTrue(reviewLikeRepository.findByMemberAndReview(member1, review).isPresent(), "회원이 리뷰를 좋아요 한 내역이 존재해야 한다.");
        assertEquals(1, review.getLikeCount(), "리뷰의 likeCount 1 증가");
    }

    @Test
    @DisplayName("리뷰 좋아요 해제")
    public void removeReviewLike() {
        //given
        Member member = Member.builder().build();
        Review review = Review.builder()
                .likeCount(1)
                .build();

        ReviewLike reviewLike = ReviewLike.init(member, review);

        memberRepository.save(member);
        reviewRepository.save(review);
        reviewLikeRepository.save(reviewLike);

        //when
        reviewLikeService.removeReviewLike(reviewLike);

        //then
        assertTrue(reviewLikeRepository.findByMemberAndReview(member, review).isEmpty(),"리뷰 좋아요 조회되지 않음");
        assertEquals(0, review.getLikeCount(), "리뷰 좋아요 수 1 감소");
    }

}