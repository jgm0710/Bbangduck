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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

class ReviewLikeServiceUnitTest {

    ReviewLikeRepository reviewLikeRepository = Mockito.mock(ReviewLikeRepository.class);
    ReviewLikeQueryRepository reviewLikeQueryRepository = Mockito.mock(ReviewLikeQueryRepository.class);
    ReviewRepository reviewRepository = Mockito.mock(ReviewRepository.class);

    ReviewLikeService reviewLikeService = new ReviewLikeService(
            reviewLikeRepository,
            reviewLikeQueryRepository,
            reviewRepository
    );

    @Test
    @DisplayName("리뷰 좋아요 등록 - 이미 좋아요를 등록한 리뷰")
    public void addLikeToReview_AlreadyLike() {
        //given
        Member member1 = Member.builder()
                .id(1L)
                .build();

        Member member2 = Member.builder()
                .id(2L)
                .build();


        Review review = Review.builder()
                .id(1L)
                .member(member1)
                .build();

        ReviewLike reviewLike = ReviewLike.init(member2, review);

        given(reviewLikeQueryRepository.findByMemberIdAndReviewId(member2.getId(), review.getId())).willReturn(Optional.of(reviewLike));

        //when

        //then
        assertThrows(ReviewHasAlreadyBeenLikedException.class, () -> reviewLikeService.addLikeToReview(member2, review));

    }

    @Test
    @DisplayName("리뷰 좋아요 등록 - 내가 생성한 리뷰일 경우")
    public void addLikeToReview_MyReview() {
        //given
        Member member = Member.builder()
                .id(1L)
                .build();


        Review review = Review.builder()
                .id(1L)
                .member(member)
                .build();

        ReviewLike reviewLike = ReviewLike.init(member, review);

        given(reviewLikeQueryRepository.findByMemberIdAndReviewId(member.getId(), review.getId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(AddLikeToMyReviewException.class, () -> reviewLikeService.addLikeToReview(member, review));

    }

    @Test
    @DisplayName("리뷰 좋아요 조회 - 리뷰 좋아요 내역을 찾을 수 없는 경우")
    public void getReviewLike_NotFound() {
        //given
        given(reviewLikeQueryRepository.findByMemberIdAndReviewId(anyLong(), anyLong())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(ReviewLikeNotFoundException.class, () -> reviewLikeService.getReviewLike(1L, 1L));

    }

}