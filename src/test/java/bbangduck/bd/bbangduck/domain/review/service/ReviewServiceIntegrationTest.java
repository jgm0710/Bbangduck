package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.auth.dto.service.MemberSignUpDto;
import bbangduck.bd.bbangduck.domain.genre.Genre;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.Satisfaction;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewSurveyCreateDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewDetail;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewPlayTogether;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewSurvey;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewHintUsageCount;
import bbangduck.bd.bbangduck.domain.review.exception.ReviewNotFoundException;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewRepository;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import bbangduck.bd.bbangduck.global.common.NullCheckUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ReviewService 통합 테스트")
class ReviewServiceIntegrationTest extends BaseTest {

    @Autowired
    ReviewService reviewService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    EntityManager em;

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAll();
        themeRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("리뷰 저장")
    public void saveReview() {
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("sritmember321321@email.com")
                .nickname("sritmember3217893")
                .build();
        Member member = Member.signUp(memberSignUpDto, 14);

        Member savedMember = memberRepository.save(member);


        Theme theme = Theme.builder()
                .name("theme")
                .genre(Genre.ACTION)
                .build();

        Theme savedTheme = themeRepository.save(theme);

        ReviewCreateDto reviewCreateDto = new ReviewCreateDto(true, LocalTime.of(0, 40), ReviewHintUsageCount.TWO, 4, null);


        //when
        Long savedReviewId = reviewService.saveReview(savedMember, savedTheme, reviewCreateDto);

        //then
        Review savedReview = reviewRepository.findById(savedReviewId).orElseThrow(ReviewNotFoundException::new);

        assertEquals(1, savedReview.getRecodeNumber());
        Member savedReviewMember = savedReview.getMember();
        assertEquals(savedMember.getId(), savedReviewMember.getId());
        Theme savedReviewTheme = savedReview.getTheme();
        assertEquals(savedTheme.getId(), savedReviewTheme.getId());
        assertEquals(reviewCreateDto.isClearYN(), savedReview.isClearYN());
        assertEquals(reviewCreateDto.getClearTime(), savedReview.getClearTime());
        assertEquals(reviewCreateDto.getHintUsageCount(), savedReview.getHintUsageCount());
    }

    @Test
    @DisplayName("리뷰의 리뷰 상세 클리어")
    public void clearReviewDetail() {
        //given
        Review review = Review.builder()
                .build();

        ReviewDetail reviewDetail = ReviewDetail.builder()
                .build();

        review.addReviewDetail(reviewDetail);

        reviewRepository.save(review);

        //when
        reviewService.clearReviewDetail(review);

        //then
        Review findReview = reviewRepository.findById(review.getId()).orElseThrow(ReviewNotFoundException::new);
        Assertions.assertNull(findReview.getReviewDetail(), "리뷰의 리뷰 상세를 클리어 했기 때문에 리뷰 상세는 Null 이 나와야 한다.");

    }

    @Test
    @DisplayName("리뷰 설문 추가")
    @Transactional
    public void addSurveyToReview() {
        //given
        Review review = Review.builder()
                .build();
        Review savedReview = reviewRepository.save(review);

        List<Genre> genres = List.of(Genre.ACTION, Genre.ARCADE);

        ReviewSurveyCreateDto reviewSurveyCreateDto = ReviewSurveyCreateDto.builder()
                .perceivedThemeGenres(genres)
                .perceivedDifficulty(Difficulty.EASY)
                .perceivedHorrorGrade(HorrorGrade.VERY_HORROR)
                .perceivedActivity(Activity.VERY_ACTIVITY)
                .scenarioSatisfaction(Satisfaction.GOOD)
                .interiorSatisfaction(Satisfaction.GOOD)
                .problemConfigurationSatisfaction(Satisfaction.GOOD)
                .build();

        //when
        System.out.println("========================================================================");
        reviewService.addSurveyToReview(review, reviewSurveyCreateDto);
        em.flush();
        em.clear();
        System.out.println("========================================================================");


        //then
        Review findReview = reviewRepository.findById(savedReview.getId()).orElseThrow(ReviewNotFoundException::new);
        ReviewSurvey reviewSurvey = findReview.getReviewSurvey();

        assertEquals(reviewSurveyCreateDto.getPerceivedDifficulty(), reviewSurvey.getPerceivedDifficulty());
        assertEquals(reviewSurveyCreateDto.getPerceivedHorrorGrade(), reviewSurvey.getPerceivedHorrorGrade());
        assertEquals(reviewSurveyCreateDto.getPerceivedActivity(), reviewSurvey.getPerceivedActivity());
        assertEquals(reviewSurveyCreateDto.getScenarioSatisfaction(), reviewSurvey.getScenarioSatisfaction());
        assertEquals(reviewSurveyCreateDto.getInteriorSatisfaction(), reviewSurvey.getInteriorSatisfaction());
        assertEquals(reviewSurveyCreateDto.getProblemConfigurationSatisfaction(), reviewSurvey.getProblemConfigurationSatisfaction());
        reviewSurvey.getPerceivedThemeGenres().forEach(genre -> assertTrue(genres.stream().anyMatch(genre1 -> genre1 == genre)));

        //final
        reviewRepository.delete(review);
    }

    @Test
    @DisplayName("리뷰 친구 목록 클리어")
    @Transactional
    public void clearReviewPlayTogether() {
        //given
        Member friend1 = Member.builder()
                .email("crpmember1@email.com")
                .nickname("crpmember1")
                .build();

        Member friend2 = Member.builder()
                .email("crpmember2@email.com")
                .nickname("crpmember2")
                .build();

        Member friend3 = Member.builder()
                .email("crpmember3@email.com")
                .nickname("crpmember3")
                .build();

        Member friend4 = Member.builder()
                .email("crpmember4@email.com")
                .nickname("crpmember4")
                .build();

        List<Member> friends = List.of(friend1, friend2, friend3, friend4);
        memberRepository.saveAll(friends);

        Review review = Review.builder()
                .build();

        friends.forEach(review::addPlayTogether);
        reviewRepository.save(review);

        em.flush();
        em.clear();

        //when
        reviewService.clearReviewPlayTogether(review);
        em.flush();
        em.clear();

        //then
        Review findReview = reviewRepository.findById(review.getId()).orElseThrow(ReviewNotFoundException::new);
        List<ReviewPlayTogether> reviewPlayTogetherEntities = findReview.getReviewPlayTogetherEntities();
        assertFalse(NullCheckUtils.existsList(reviewPlayTogetherEntities), "조회된 리뷰의 함께 플레이한 친구는 비어있어야 한다.");
    }

}