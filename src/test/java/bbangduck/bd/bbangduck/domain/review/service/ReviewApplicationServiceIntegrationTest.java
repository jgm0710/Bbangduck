package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.service.MemberPlayInclinationService;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewHintUsageCount;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.service.ThemeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalTime;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("ReviewApplicationService 단위 테스트")
class ReviewApplicationServiceIntegrationTest {
    ReviewService reviewMockService = Mockito.mock(ReviewService.class);
    ThemeService themeMockService = Mockito.mock(ThemeService.class);
    MemberService memberMockService = Mockito.mock(MemberService.class);
    MemberPlayInclinationService memberPlayInclinationMockService = Mockito.mock(MemberPlayInclinationService.class);

    ReviewApplicationService reviewMockApplicationService = new ReviewApplicationService(
            reviewMockService,
            themeMockService,
            memberMockService,
            memberPlayInclinationMockService
    );

    @Test
    @DisplayName("리뷰 생성")
    public void createReview() {
        //given
        Long memberId = 1L;
        Long themeId = 1L;
        ReviewCreateDto reviewCreateDto = new ReviewCreateDto(true, LocalTime.of(0, 40), ReviewHintUsageCount.THREE_OR_MORE, 3, null);

        Member member = Member.builder()
                .id(1L)
                .email("member@emailcom")
                .nickname("member")
                .build();

        given(memberMockService.getMember(memberId)).willReturn(member);

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

        given(themeMockService.getTheme(themeId)).willReturn(theme);

        Long reviewId = 1L;
        given(reviewMockService.saveReview(member, theme, reviewCreateDto)).willReturn(reviewId);

        Review review = Review.builder()
                .id(reviewId)
                .clearYN(reviewCreateDto.isClearYN())
                .clearTime(reviewCreateDto.getClearTime())
                .hintUsageCount(reviewCreateDto.getHintUsageCount())
                .rating(reviewCreateDto.getRating())
                .build();

        given(reviewMockService.getReview(reviewId)).willReturn(review);


        //when
        reviewMockApplicationService.createReview(memberId, themeId, reviewCreateDto);

        //then
        verify(memberMockService).getMember(memberId);
        verify(themeMockService).getTheme(themeId);
        verify(reviewMockService).saveReview(member, theme, reviewCreateDto);
        verify(reviewMockService).getReview(reviewId);
        verify(reviewMockService).addPlayTogetherFriendsToReview(review, null);
        verify(memberPlayInclinationMockService).reflectingPropensityOfMemberToPlay(member, theme.getGenres());
        verify(themeMockService).reflectThemeRating(theme, reviewCreateDto.getRating());
    }

}