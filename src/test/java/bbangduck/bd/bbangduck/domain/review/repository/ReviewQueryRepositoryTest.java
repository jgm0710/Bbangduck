package bbangduck.bd.bbangduck.domain.review.repository;

import bbangduck.bd.bbangduck.domain.auth.dto.controller.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.review.dto.controller.request.ReviewCreateRequestDto;
import bbangduck.bd.bbangduck.domain.review.dto.entity.ReviewRecodesCountsDto;
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

        reviewService.deleteReview(member1Review1Id);
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

}