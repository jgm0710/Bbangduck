package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.request.ReviewCreateRequestDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.member.BaseJGMServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("리뷰 좋아요 Service 테스트")
class ReviewLikeServiceTest extends BaseJGMServiceTest {

    @Test
    @DisplayName("리뷰에 좋아요 등록")
    public void addLikeToReview() {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long member1Id = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        memberSignUpRequestDto.setEmail("member2@email.com");
        memberSignUpRequestDto.setNickname("member2");
        memberSignUpRequestDto.setSocialId("382109321789");
        Long member2Id = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(null);
        Long reviewId = reviewService.createReview(member1Id, themeSample.getId(), reviewCreateRequestDto.toServiceDto());

        //when
        reviewLikeService.addLikeToReview(member2Id, reviewId);

        //then
        boolean existsReviewLike = reviewLikeService.getExistsReviewLike(member2Id, reviewId);
        assertTrue(existsReviewLike, "리뷰에 좋아요 등록 후 리뷰 좋아요를 조회하면 리뷰 좋아요가 나와야 한다.");

    }

    @Test
    @DisplayName("리뷰에 등록된 좋아요 제거")
    public void removeLikeFromReview() {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long member1Id = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        memberSignUpRequestDto.setEmail("member2@email.com");
        memberSignUpRequestDto.setNickname("member2");
        memberSignUpRequestDto.setSocialId("382109321789");
        Long member2Id = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(null);
        Long reviewId = reviewService.createReview(member1Id, themeSample.getId(), reviewCreateRequestDto.toServiceDto());

        reviewLikeService.addLikeToReview(member2Id, reviewId);

        boolean existsReviewLike = reviewLikeService.getExistsReviewLike(member2Id, reviewId);
        assertTrue(existsReviewLike, "리뷰에 좋아요가 등록되어 있어야 한다.");

        //when
        reviewLikeService.removeLikeFromReview(member2Id, reviewId);

        //then
        boolean existsReviewLike2 = reviewLikeService.getExistsReviewLike(member2Id, reviewId);
        assertFalse(existsReviewLike2, "리뷰에 좋아요가 등록되어 있지 않아야 한다.");

    }

}