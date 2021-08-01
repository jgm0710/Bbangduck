package bbangduck.bd.bbangduck.domain.review.repository;

import bbangduck.bd.bbangduck.domain.auth.dto.controller.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.dto.controller.request.ReviewCreateRequestDto;
import bbangduck.bd.bbangduck.domain.review.dto.entity.ReviewRecodesCountsDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.member.BaseJGMServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReviewQueryRepositoryTest extends BaseJGMServiceTest {

    @Test
    @DisplayName("회원 리뷰 기록 조회")
    @Transactional
    public void findRecodesCountsOfMember() {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(null);
        Long member1Review1Id = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());
        reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        simpleReviewCreateRequestDto.setClearYN(false);
        reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());
        reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());
        reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        memberSignUpRequestDto.setEmail("test2@email.com");
        memberSignUpRequestDto.setNickname("test2");
        memberSignUpRequestDto.setSocialId("333332213");
        Long signUp2 = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        reviewApplicationService.createReview(signUp2, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());
        reviewApplicationService.createReview(signUp2, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());
        reviewApplicationService.createReview(signUp2, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        em.flush();
        em.clear();

        reviewApplicationService.deleteReview(signUpId, member1Review1Id);
        em.flush();
        em.clear();

        //when
        ReviewRecodesCountsDto reviewRecodesCountsDto = reviewQueryRepository.findRecodesCountsByMember(signUpId).orElse(new ReviewRecodesCountsDto());
        ReviewRecodesCountsDto recodesCountsByMember2 = reviewQueryRepository.findRecodesCountsByMember(signUp2).orElse(new ReviewRecodesCountsDto());

        //then
        assertEquals(4, reviewRecodesCountsDto.getTotalRecodesCount(),
                "총 리뷰는 5개 생성하고 1개 삭제했으므로 totalRecodesCount 는 4가 나와야한다.");
        assertEquals(1, reviewRecodesCountsDto.getSuccessRecodesCount(),
                "성공한 리뷰는 2개 생성하고 1개 삭제했으므로 successRecodesCount 는 1이 나와야한다.");
        assertEquals(3, reviewRecodesCountsDto.getFailRecodesCount(),
                "실패한 리뷰는 3개 생성했으므로 failRecodesCount 는 3이 나와야한다.");

        assertEquals(3, recodesCountsByMember2.getTotalRecodesCount(), "회원 2번의 총 리뷰 개수는 3개");
        assertEquals(0,recodesCountsByMember2.getSuccessRecodesCount(),"회원 2번의 성공 리뷰 개수는 0개");
        assertEquals(3,recodesCountsByMember2.getFailRecodesCount(), "회원 2번의 총 실패 리뷰 개수는 3개");

    }

    @Test
    @DisplayName("특정 회원이 테마에 등록한 리뷰 개수 조회")
    public void getReviewsCountByMemberIdAndThemeId() {
        //given
        Member member = Member.builder().build();
        Theme theme = Theme.builder().build();

        Review review1 = Review.builder()
                .member(member)
                .theme(theme)
                .deleteYN(false)
                .build();
        Review review2 = Review.builder()
                .member(member)
                .theme(theme)
                .deleteYN(false)
                .build();
        Review review3 = Review.builder()
                .member(member)
                .theme(theme)
                .deleteYN(true)
                .build();

        memberRepository.save(member);
        themeRepository.save(theme);
        reviewRepository.save(review1);
        reviewRepository.save(review2);
        reviewRepository.save(review3);

        Member member2 = Member.builder().build();
        Theme theme2 = Theme.builder().build();

        Review review4 = Review.builder()
                .member(member2)
                .theme(theme2)
                .deleteYN(false)
                .build();
        Review review5 = Review.builder()
                .member(member2)
                .theme(theme2)
                .deleteYN(true)
                .build();

        memberRepository.save(member2);
        themeRepository.save(theme2);
        reviewRepository.save(review4);
        reviewRepository.save(review5);

        //when
        long reviewsCount = reviewQueryRepository.getReviewsCountByMemberIdAndThemeId(member.getId(), theme.getId());

        //then
        assertEquals(2, reviewsCount, "member 가 theme 에 생성한 리뷰 개수는 총 3개이고, 하나가 삭제된 리뷰이므로 조회되는 개수는 2개이다.");

    }

}