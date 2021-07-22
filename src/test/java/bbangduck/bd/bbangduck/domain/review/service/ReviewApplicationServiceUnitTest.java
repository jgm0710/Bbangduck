package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.follow.exception.NotTwoWayFollowRelationException;
import bbangduck.bd.bbangduck.domain.follow.service.FollowService;
import bbangduck.bd.bbangduck.domain.genre.Genre;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.service.MemberPlayInclinationService;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.review.dto.service.*;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewSurvey;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewHintUsageCount;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemePlayMember;
import bbangduck.bd.bbangduck.domain.theme.service.ThemeAnalysisService;
import bbangduck.bd.bbangduck.domain.theme.service.ThemePlayMemberService;
import bbangduck.bd.bbangduck.domain.theme.service.ThemeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@DisplayName("ReviewApplicationService 단위 테스트")
class ReviewApplicationServiceUnitTest {

    MemberService memberMockService = mock(MemberService.class);
    MemberPlayInclinationService memberPlayInclinationMockService = mock(MemberPlayInclinationService.class);
    ReviewService reviewMockService = mock(ReviewService.class);
    ReviewLikeService reviewLikeMockService = mock(ReviewLikeService.class);
    ThemeService themeMockService = mock(ThemeService.class);
    ThemeAnalysisService themeAnalysisMockService = mock(ThemeAnalysisService.class);
    ThemePlayMemberService themePlayMemberMockService = mock(ThemePlayMemberService.class);
    FollowService followMockService = mock(FollowService.class);

    ReviewApplicationService reviewMockApplicationService = new ReviewApplicationService(
            memberMockService,
            memberPlayInclinationMockService,
            reviewMockService,
            reviewLikeMockService,
            themeMockService,
            themeAnalysisMockService,
            themePlayMemberMockService,
            followMockService
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
                .genre(Genre.ACTION)
                .build();

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
        given(followMockService.isTwoWayFollowRelationMembers(memberId, friendIds)).willReturn(true);
        given(memberMockService.getMembers(friendIds)).willReturn(friends);


        //when
        reviewMockApplicationService.createReview(memberId, themeId, reviewCreateDto);

        //then
        then(memberMockService).should(times(1)).getMember(memberId);
        then(themeMockService).should(times(1)).getTheme(themeId);
        then(reviewMockService).should(times(1)).saveReview(member, theme, reviewCreateDto);
        then(reviewMockService).should(times(1)).getReview(reviewId);
        then(followMockService).should(times(1)).isTwoWayFollowRelationMembers(memberId, friendIds);
        then(memberMockService).should(times(1)).getMembers(friendIds);
        then(followMockService).should(times(0)).isTwoWayFollowRelation(memberId, friendIds.get(0));
        then(reviewMockService).should(times(1)).addPlayTogetherFriendsToReview(review, friends);
        then(memberPlayInclinationMockService).should(times(1)).reflectingPropensityOfMemberToPlay(member, theme.getGenre());
        then(themeMockService).should(times(1)).increaseThemeRating(theme, reviewCreateDto.getRating());
        then(themePlayMemberMockService).should(times(1)).playTheme(theme, member);
    }

    @Test
    @DisplayName("리뷰 생성 - 리뷰 생성 시 추가하는 친구가 친구 관계가 아닌 경우")
    public void createReview_RegisterNotFriend() {
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
                .genre(Genre.ACTION)
                .build();

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
        given(followMockService.isTwoWayFollowRelationMembers(memberId, friendIds)).willReturn(false);
        given(followMockService.isTwoWayFollowRelation(memberId, friendIds.get(1))).willReturn(false);


        //when

        //then
        assertThrows(NotTwoWayFollowRelationException.class, () -> reviewMockApplicationService.createReview(memberId, themeId, reviewCreateDto));

        then(memberMockService).should(times(1)).getMember(memberId);
        then(themeMockService).should(times(1)).getTheme(themeId);

        then(followMockService).should(times(1)).isTwoWayFollowRelationMembers(memberId, friendIds);
        then(memberMockService).should(times(0)).getMembers(friendIds);
        then(followMockService).should(times(1)).isTwoWayFollowRelation(memberId, friendIds.get(0));
        then(followMockService).should(times(0)).isTwoWayFollowRelation(memberId, friendIds.get(1));

        then(reviewMockService).should(times(0)).saveReview(member, theme, reviewCreateDto);
        then(reviewMockService).should(times(0)).getReview(reviewId);
        then(reviewMockService).should(times(0)).addPlayTogetherFriendsToReview(review, friends);
        then(memberPlayInclinationMockService).should(times(0)).reflectingPropensityOfMemberToPlay(member, theme.getGenre());
        then(themeMockService).should(times(0)).increaseThemeRating(theme, reviewCreateDto.getRating());

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
        given(followMockService.isTwoWayFollowRelationMembers(member.getId(), friendIds)).willReturn(true);
        given(memberMockService.getMembers(friendIds)).willReturn(friends);

        //when
        reviewMockApplicationService.updateReview(review.getId(), member.getId(), reviewUpdateDto);

        //then
        then(reviewMockService).should(times(1)).getReview(review.getId());
        then(reviewMockService).should(times(1)).checkIfMyReview(member.getId(), review.getId());
        then(themeMockService).should(times(1)).updateThemeRating(theme, review.getRating(), reviewUpdateDto.getRating());

        then(reviewMockService).should(times(1)).clearReviewPlayTogether(review);
        then(reviewMockService).should(times(1)).clearReviewDetail(review);

        then(reviewMockService).should(times(1)).updateReviewBase(review, reviewUpdateDto.toReviewUpdateBaseDto());

        then(followMockService).should(times(1)).isTwoWayFollowRelationMembers(member.getId(), friendIds);
        then(memberMockService).should(times(1)).getMembers(friendIds);
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
        given(followMockService.isTwoWayFollowRelationMembers(member.getId(), friendIds)).willReturn(true);
        given(memberMockService.getMembers(friendIds)).willReturn(friends);
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
        then(followMockService).should(times(1)).isTwoWayFollowRelationMembers(member.getId(), friendIds);
        then(memberMockService).should(times(1)).getMembers(friendIds);
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


        List<Genre> genres = List.of(Genre.ACTION, Genre.ADVENTURE);


        ReviewSurveyCreateDto reviewSurveyCreateDto = ReviewSurveyCreateDto.builder()
                .perceivedThemeGenres(genres)
                .build();

        given(reviewMockService.getReview(review.getId())).willReturn(review);

        //when
        reviewMockApplicationService.addSurveyToReview(review.getId(), member.getId(), reviewSurveyCreateDto);

        //then
        then(reviewMockService).should().getReview(review.getId());
        then(reviewMockService).should().checkIfMyReview(review.getId(), member.getId());
        then(reviewMockService).should().addSurveyToReview(review, reviewSurveyCreateDto);
        then(themeAnalysisMockService).should().reflectingThemeAnalyses(theme, genres);
    }

    @Test
    @DisplayName("리뷰에 좋아요 추가")
    public void addLikeToReview() {
        //given
        Member member = Member.builder()
                .id(1L)
                .build();

        Member member2 = Member.builder()
                .id(2L)
                .build();

        Theme theme = Theme.builder()
                .id(1L)
                .build();

        Review review = Review.builder()
                .id(1L)
                .member(member)
                .theme(theme)
                .build();

        ThemePlayMember themePlayMember = ThemePlayMember.builder()
                .id(1L)
                .member(member)
                .theme(theme)
                .build();


        given(memberMockService.getMember(member2.getId())).willReturn(member2);
        given(reviewMockService.getReview(review.getId())).willReturn(review);
        given(themePlayMemberMockService.getThemePlayMember(theme.getId(), member.getId())).willReturn(themePlayMember);

        //when
        reviewMockApplicationService.addLikeToReview(member2.getId(), review.getId());

        //then
        then(memberMockService).should(times(1)).getMember(member2.getId());
        then(reviewMockService).should(times(1)).getReview(review.getId());

        then(reviewLikeMockService).should(times(1)).addLikeToReview(member2, review);

        then(themePlayMemberMockService).should(times(1)).getThemePlayMember(member.getId(), theme.getId());

        assertEquals(1, themePlayMember.getReviewLikeCount());

    }

}