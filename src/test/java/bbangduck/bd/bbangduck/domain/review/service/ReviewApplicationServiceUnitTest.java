package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.friend.service.MemberFriendService;
import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.genre.service.GenreService;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.service.MemberPlayInclinationService;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.review.dto.service.*;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewSurvey;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewHintUsageCount;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.service.ThemeAnalysisService;
import bbangduck.bd.bbangduck.domain.theme.service.ThemeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@DisplayName("ReviewApplicationService 단위 테스트")
class ReviewApplicationServiceUnitTest {

    ReviewService reviewMockService = mock(ReviewService.class);
    ThemeService themeMockService = mock(ThemeService.class);
    MemberService memberMockService = mock(MemberService.class);
    MemberPlayInclinationService memberPlayInclinationMockService = mock(MemberPlayInclinationService.class);
    MemberFriendService memberFriendMockService = mock(MemberFriendService.class);
    GenreService genreMockService = mock(GenreService.class);
    ThemeAnalysisService themeAnalysisMockService = mock(ThemeAnalysisService.class);

    ReviewApplicationService reviewMockApplicationService = new ReviewApplicationService(
            reviewMockService,
            themeMockService,
            memberMockService,
            memberPlayInclinationMockService,
            memberFriendMockService,
            genreMockService,
            themeAnalysisMockService
    );



    @Test
    @DisplayName("리뷰 생성")
    public void createReview() {
        //given
        Long memberId = 1L;
        Long themeId = 1L;

        Member member = Member.builder()
                .id(1L)
                .email("member@emailcom")
                .nickname("member")
                .build();

        Theme theme = Theme.builder()
                .id(1L)
                .name("theme")
                .build();

        Genre genre1 = Genre.builder()
                .code("GR1")
                .name("genre1")
                .build();

        Genre genre2 = Genre.builder()
                .code("GR2")
                .name("genre2")
                .build();

        theme.addGenre(genre1);
        theme.addGenre(genre2);

        Long reviewId = 1L;

        Member friend1 = Member.builder()
                .id(3L)
                .build();

        Member friend2 = Member.builder()
                .id(5L)
                .build();

        List<Member> friends = List.of(friend1, friend2);
        List<Long> friendIds = List.of(friend1.getId(), friend2.getId());

        ReviewCreateDto reviewCreateDto = new ReviewCreateDto(true, LocalTime.of(0, 40), ReviewHintUsageCount.THREE_OR_MORE, 3, friendIds);

        Review review = Review.builder()
                .id(reviewId)
                .clearYN(reviewCreateDto.isClearYN())
                .clearTime(reviewCreateDto.getClearTime())
                .hintUsageCount(reviewCreateDto.getHintUsageCount())
                .rating(reviewCreateDto.getRating())
                .build();

        given(memberMockService.getMember(memberId)).willReturn(member);
        given(themeMockService.getTheme(themeId)).willReturn(theme);
        given(reviewMockService.saveReview(member, theme, reviewCreateDto)).willReturn(reviewId);
        given(reviewMockService.getReview(reviewId)).willReturn(review);
        given(memberFriendMockService.getAcceptedFriends(memberId, friendIds)).willReturn(friends);


        //when
        reviewMockApplicationService.createReview(memberId, themeId, reviewCreateDto);

        //then
        then(memberMockService).should(times(1)).getMember(memberId);
        then(themeMockService).should(times(1)).getTheme(themeId);
        then(reviewMockService).should(times(1)).saveReview(member, theme, reviewCreateDto);
        then(reviewMockService).should(times(1)).getReview(reviewId);
        then(memberFriendMockService).should().getAcceptedFriends(memberId, friendIds);
        then(reviewMockService).should(times(1)).addPlayTogetherFriendsToReview(review, friends);
        then(memberPlayInclinationMockService).should(times(1)).reflectingPropensityOfMemberToPlay(member, theme.getGenres());
        then(themeMockService).should(times(1)).increaseThemeRating(theme, reviewCreateDto.getRating());
    }

    @Test
    @DisplayName("리뷰 수정 - Base 리뷰로 수정")
    public void updateReview_ToBase() {
        //given
        Member member = Member.builder()
                .id(1L)
                .build();

        Theme theme = Theme.builder()
                .id(1L)
                .build();

        Review review = Review.builder()
                .id(1L)
                .reviewType(ReviewType.BASE)
                .member(member)
                .theme(theme)
                .rating(3)
                .build();

        List<Member> friends = new ArrayList<>();

        for (long i = 2; i < 5; i++) {
            Member friend = Member.builder()
                    .id(i)
                    .build();

            friends.add(friend);
        }

        List<Long> friendIds = friends.stream().map(Member::getId).collect(Collectors.toList());

        List<ReviewImageDto> reviewImageDtos = new ArrayList<>();
        for (long i = 0; i < 3; i++) {
            ReviewImageDto reviewImageDto = ReviewImageDto.builder()
                    .fileStorageId(i)
                    .fileName("fileName" + i)
                    .build();
            reviewImageDtos.add(reviewImageDto);
        }

        ReviewUpdateDto reviewUpdateDto = new ReviewUpdateDto(ReviewType.BASE,
                true,
                LocalTime.of(1, 0),
                ReviewHintUsageCount.TWO,
                4,
                friendIds,
                reviewImageDtos,
                "update comment");

        given(reviewMockService.getReview(review.getId())).willReturn(review);
        given(memberFriendMockService.getAcceptedFriends(member.getId(), friendIds)).willReturn(friends);

        //when
        reviewMockApplicationService.updateReview(review.getId(), member.getId(), reviewUpdateDto);

        //then
        then(reviewMockService).should(times(1)).getReview(review.getId());
        then(reviewMockService).should(times(1)).checkIfMyReview(member.getId(), review.getId());
        then(themeMockService).should(times(1)).updateThemeRating(theme, review.getRating(), reviewUpdateDto.getRating());

        then(reviewMockService).should(times(1)).clearReviewPlayTogether(review);
        then(reviewMockService).should(times(1)).clearReviewDetail(review);

        then(reviewMockService).should(times(1)).updateReviewBase(review, reviewUpdateDto.toReviewUpdateBaseDto());
        then(memberFriendMockService).should(times(1)).getAcceptedFriends(member.getId(), friendIds);
        then(reviewMockService).should(times(1)).addPlayTogetherFriendsToReview(review, friends);

        then(reviewMockService).should(times(0)).addDetailToReview(review, reviewUpdateDto.toReviewDetailCreateDto());
    }

    @Test
    @DisplayName("리뷰 수정 - Detail 리뷰로 수정")
    public void updateReview_ToDetail() {
        //given
        Member member = Member.builder()
                .id(1L)
                .build();

        Theme theme = Theme.builder()
                .id(1L)
                .build();

        Review review = Review.builder()
                .id(1L)
                .reviewType(ReviewType.BASE)
                .member(member)
                .theme(theme)
                .rating(3)
                .build();

        List<Member> friends = new ArrayList<>();

        for (long i = 2; i < 5; i++) {
            Member friend = Member.builder()
                    .id(i)
                    .build();

            friends.add(friend);
        }

        List<Long> friendIds = friends.stream().map(Member::getId).collect(Collectors.toList());

        List<ReviewImageDto> reviewImageDtos = new ArrayList<>();
        for (long i = 0; i < 3; i++) {
            ReviewImageDto reviewImageDto = ReviewImageDto.builder()
                    .fileStorageId(i)
                    .fileName("fileName" + i)
                    .build();
            reviewImageDtos.add(reviewImageDto);
        }

        ReviewUpdateDto reviewUpdateDto = new ReviewUpdateDto(ReviewType.DETAIL,
                true,
                LocalTime.of(1, 0),
                ReviewHintUsageCount.TWO,
                4,
                friendIds,
                reviewImageDtos,
                "update comment");

        ReviewUpdateDto mockReviewUpdateDto = mock(ReviewUpdateDto.class);

        given(reviewMockService.getReview(review.getId())).willReturn(review);
        given(memberFriendMockService.getAcceptedFriends(member.getId(), friendIds)).willReturn(friends);
        given(mockReviewUpdateDto.getRating()).willReturn(reviewUpdateDto.getRating());
        ReviewUpdateBaseDto reviewUpdateBaseDto = reviewUpdateDto.toReviewUpdateBaseDto();
        given(mockReviewUpdateDto.toReviewUpdateBaseDto()).willReturn(reviewUpdateBaseDto);
        given(mockReviewUpdateDto.getFriendIds()).willReturn(reviewUpdateDto.getFriendIds());
        ReviewDetailCreateDto reviewDetailCreateDto = reviewUpdateDto.toReviewDetailCreateDto();
        given(mockReviewUpdateDto.toReviewDetailCreateDto()).willReturn(reviewDetailCreateDto);
        given(mockReviewUpdateDto.getReviewType()).willReturn(reviewUpdateDto.getReviewType());

        //when
        reviewMockApplicationService.updateReview(review.getId(), member.getId(), mockReviewUpdateDto);

        //then
        then(reviewMockService).should(times(1)).getReview(review.getId());
        then(reviewMockService).should(times(1)).checkIfMyReview(member.getId(), review.getId());
        then(themeMockService).should(times(1)).updateThemeRating(theme, review.getRating(), reviewUpdateDto.getRating());

        then(reviewMockService).should(times(1)).clearReviewPlayTogether(review);
        then(reviewMockService).should(times(1)).clearReviewDetail(review);

        then(reviewMockService).should(times(1)).updateReviewBase(review, reviewUpdateBaseDto);
        then(memberFriendMockService).should(times(1)).getAcceptedFriends(member.getId(), friendIds);
        then(reviewMockService).should(times(1)).addPlayTogetherFriendsToReview(review, friends);

        then(reviewMockService).should(times(1)).addDetailToReview(review, reviewDetailCreateDto);
    }

    @Test
    @DisplayName("리뷰에 설문 추가")
    public void addDetailToReview() {
        //given
        ReviewImageDto reviewImageDto = new ReviewImageDto(1L, "fileName");
        List<ReviewImageDto> reviewImageDtos = List.of(reviewImageDto);
        ReviewDetailCreateDto reviewDetailCreateDto = new ReviewDetailCreateDto(reviewImageDtos, "comment");

        Member member = Member.builder()
                .id(1L)
                .build();

        Review review = Review.builder()
                .id(1L)
                .member(member)
                .deleteYN(false)
                .build();

        given(reviewMockService.getReview(review.getId())).willReturn(review);

        //when
        reviewMockApplicationService.addDetailToReview(review.getId(), member.getId(), reviewDetailCreateDto);

        //then
        then(reviewMockService).should(times(1)).getReview(review.getId());
        then(reviewMockService).should(times(1)).checkIfMyReview(member.getId(), member.getId());
        then(reviewMockService).should(times(1)).addDetailToReview(review, reviewDetailCreateDto);
    }

    @Test
    @DisplayName("리뷰에 설문 추가")
    public void addSurveyToReview() {
        //given
        Member member = Member.builder()
                .id(1L)
                .build();

        Theme theme = Theme.builder()
                .id(1L)
                .deleteYN(false)
                .build();

        Review review = Review.builder()
                .id(1L)
                .member(member)
                .theme(theme)
                .deleteYN(false)
                .build();

        ReviewSurvey reviewSurvey = ReviewSurvey.builder()
                .id(1L)
                .build();

        review.addReviewSurvey(reviewSurvey);

        Genre genre1 = Genre.builder()
                .id(1L)
                .code("genre1")
                .name("genre1")
                .build();

        Genre genre2 = Genre.builder()
                .id(2L)
                .code("genre2")
                .name("genre2")
                .build();

        List<Genre> genres = List.of(genre1, genre2);
        List<String> genreCodes = List.of(genre1.getCode(), genre2.getCode());


        ReviewSurveyCreateDto reviewSurveyCreateDto = ReviewSurveyCreateDto.builder()
                .genreCodes(genreCodes)
                .build();

        given(reviewMockService.getReview(review.getId())).willReturn(review);
        given(genreMockService.getGenresByCodes(genreCodes)).willReturn(genres);

        //when
        reviewMockApplicationService.addSurveyToReview(review.getId(), member.getId(), reviewSurveyCreateDto);

        //then
        then(reviewMockService).should().getReview(review.getId());
        then(reviewMockService).should().checkIfMyReview(review.getId(), member.getId());
        then(genreMockService).should().getGenresByCodes(genreCodes);
        then(reviewMockService).should().addSurveyToReview(review, reviewSurveyCreateDto);
        then(reviewMockService).should().addGenresToReviewSurvey(reviewSurvey, genres);
        then(themeAnalysisMockService).should().reflectingThemeAnalyses(theme, genres);
    }

}