package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewSurveyCreateRequestDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewSurveyUpdateRequestDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewSurvey;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.review.exception.ExpirationOfReviewSurveyAddPeriodException;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewRepository;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.member.BaseJGMServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("MockBean 을 주입 받는 ReviewService 로직 테스트")
class ReviewMockServiceTest extends BaseJGMServiceTest {

    @MockBean
    ReviewRepository reviewRepository;

    @Test
    @DisplayName("리뷰에 설문 정보 추가 - 설문 정보 추가 가능 기간이 만료된 경우")
    @Transactional
    public void addSurveyToReview_PeriodExpiration() {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
        Member signUpMember = memberService.getMember(signUpId);

        Theme theme = createTheme();

        Review review = Review.builder()
                .id(1L)
                .member(signUpMember)
                .theme(theme)
                .reviewType(ReviewType.SIMPLE)
                .recodeNumber(1)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 44, 44))
                .hintUsageCount(2)
                .rating(5)
                .comment("아무 코멘트")
                .likeCount(0)
                .registerTimes(LocalDateTime.now().minusDays(reviewProperties.getPeriodForAddingSurveys() + 2))
                .updateTimes(LocalDateTime.now().minusDays(reviewProperties.getPeriodForAddingSurveys() + 2))
                .build();

        given(reviewRepository.findById(any())).willReturn(Optional.of(review));

        List<String> genreCodes = createGenreCodes();

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);

        //when

        //then
        assertThrows(ExpirationOfReviewSurveyAddPeriodException.class, () -> reviewService.addSurveyToReview(1L, reviewSurveyCreateRequestDto.toServiceDto()));

    }

    @Test
    @DisplayName("리뷰에 등록된 설문 수정 - 설문 수정 가능 기간이 만료된 경우")
    public void updateSurveyFromReview_PeriodExpiration() {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
        Member signUpMember = memberService.getMember(signUpId);

        Theme theme = createTheme();

        Review review = Review.builder()
                .id(1L)
                .member(signUpMember)
                .theme(theme)
                .reviewType(ReviewType.SIMPLE)
                .recodeNumber(1)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 44, 44))
                .hintUsageCount(2)
                .rating(5)
                .comment("아무 코멘트")
                .likeCount(0)
                .registerTimes(LocalDateTime.now().minusDays(reviewProperties.getPeriodForAddingSurveys() + 2))
                .updateTimes(LocalDateTime.now().minusDays(reviewProperties.getPeriodForAddingSurveys() + 2))
                .build();


        List<String> oldGenreCodes = List.of("HR1", "RSN1");
        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(oldGenreCodes);
        ReviewSurvey reviewSurvey = ReviewSurvey.create(reviewSurveyCreateRequestDto.toServiceDto());
        review.setReviewSurvey(reviewSurvey);

        List<String> newGenreCodes = List.of("HR1", "RMC1");
        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);

        given(reviewRepository.findById(any())).willReturn(Optional.of(review));

        //when

        //then
        assertThrows(ExpirationOfReviewSurveyAddPeriodException.class, () -> reviewService.updateSurveyFromReview(1L, reviewSurveyUpdateRequestDto.toServiceDto()));

    }
}