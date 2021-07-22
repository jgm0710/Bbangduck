package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.auth.dto.controller.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.review.dto.controller.request.ReviewCreateRequestDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.exception.ReviewNotFoundException;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemePlayMemberQueryRepository;
import bbangduck.bd.bbangduck.domain.theme.service.ThemePlayMemberService;
import bbangduck.bd.bbangduck.member.BaseJGMServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("리뷰 좋아요 Service 테스트")
class ReviewLikeServiceTest extends BaseJGMServiceTest {

    @Autowired
    ThemePlayMemberQueryRepository themePlayMemberQueryRepository;

    @Autowired
    ThemePlayMemberService themePlayMemberService;

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
        Long reviewId = reviewApplicationService.createReview(member1Id, themeSample.getId(), reviewCreateRequestDto.toServiceDto());

        //when
        reviewApplicationService.addLikeToReview(member2Id, reviewId);

        //then
        boolean existsReviewLike = reviewLikeService.isMemberLikeToReview(member2Id, reviewId);
        assertTrue(existsReviewLike, "리뷰에 좋아요 등록 후 리뷰 좋아요를 조회하면 리뷰 좋아요가 나와야 한다.");

        Review findReview = reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);
        assertEquals(1, findReview.getLikeCount());
    }
}