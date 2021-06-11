package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.service.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.Satisfaction;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewSurveyCreateRequestDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewSurveyUpdateRequestDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewSurvey;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewRepository;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.member.BaseJGMApiControllerTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReviewApiMockControllerTest extends BaseJGMApiControllerTest {

    @MockBean
    ReviewRepository reviewRepository;

    @Test
    @DisplayName("리뷰에 설문 등록 - 리뷰 생성 이후 7일이 지난 경우")
    public void addSurveyToReview_PeriodExpiration() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme theme = createTheme();
        Member signUpMember = memberService.getMember(signUpId);

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

//        reviewRepository.save(review);

        List<String> genreCodes = createGenreCodes();

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = ReviewSurveyCreateRequestDto.builder()
                .genreCodes(genreCodes)
                .perceivedDifficulty(Difficulty.EASY)
                .perceivedHorrorGrade(HorrorGrade.NORMAL)
                .perceivedActivity(Activity.NORMAL)
                .scenarioSatisfaction(Satisfaction.GOOD)
                .interiorSatisfaction(Satisfaction.VERY_BAD)
                .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                .build();

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        given(reviewRepository.findById(any())).willReturn(Optional.of(review));

        Review findReview = reviewService.getReview(1L);

        System.out.println("findReview.getRegisterTimes() = " + findReview.getRegisterTimes());


        //when
        ResultActions perform = mockMvc.perform(
                post("/api/reviews/" + 1L + "/surveys")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSurveyCreateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").value(ResponseStatus.EXPIRATION_OF_REVIEW_SURVEY_ADD_PERIOD_EXCEPTION.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message", Matchers.containsString(ResponseStatus.EXPIRATION_OF_REVIEW_SURVEY_ADD_PERIOD_EXCEPTION.getMessage())))
                .andDo(document(
                        "add-survey-to-review-period-expiration",
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data").description("[null]"),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
                        )
                ))
        ;

    }

    @Test
    @DisplayName("리뷰에 등록된 설문 수정 - 7일 이내에 리뷰를 등록하지 않았을 경우")
    public void updateSurveyFromReview_PeriodExpiration() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

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

        given(reviewRepository.findById(any())).willReturn(Optional.of(review));

        List<String> newGenreCodes = List.of("HR1", "ADVT1");
        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/reviews/" + 1L + "/surveys")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSurveyUpdateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").value(ResponseStatus.EXPIRATION_OF_REVIEW_SURVEY_ADD_PERIOD_EXCEPTION.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(Matchers.containsString(ResponseStatus.EXPIRATION_OF_REVIEW_SURVEY_ADD_PERIOD_EXCEPTION.getMessage())))
                .andDo(document(
                        "update-survey-from-review-period-expiration",
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data").description("[null]"),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
                        )

                ))
        ;

    }
}