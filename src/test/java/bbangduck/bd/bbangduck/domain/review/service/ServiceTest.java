package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.genre.repository.GenreRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberFriendQueryRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberPlayInclinationQueryRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberPlayInclinationRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.review.repository.*;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import bbangduck.bd.bbangduck.global.config.properties.ReviewProperties;
import org.junit.Test;
import org.mockito.Mockito;

public class ServiceTest {
    private ReviewRepository mockReviewRepository = Mockito.mock(ReviewRepository.class);
    private ReviewQueryRepository mockReviewQueryRepository = Mockito.mock(ReviewQueryRepository.class);
    private MemberRepository mockMemberRepository = Mockito.mock(MemberRepository.class);
    private MemberPlayInclinationRepository mockMemberPlayInclinationRepository = Mockito.mock(MemberPlayInclinationRepository.class);
    private MemberPlayInclinationQueryRepository mockMemberPlayInclinationQueryRepository = Mockito.mock(MemberPlayInclinationQueryRepository.class);
    private MemberFriendQueryRepository mockMemberFriendQueryRepository = Mockito.mock(MemberFriendQueryRepository.class);
    private ThemeRepository mockThemeRepository = Mockito.mock(ThemeRepository.class);
    private GenreRepository mockGenreRepository = Mockito.mock(GenreRepository.class);
    private ReviewProperties mockReviewProperties = Mockito.mock(ReviewProperties.class);
    private ReviewPerceivedThemeGenreRepository mockReviewPerceivedThemeGenreRepository = Mockito.mock(ReviewPerceivedThemeGenreRepository.class);
    private ReviewImageRepository mockReviewImageRepository = Mockito.mock(ReviewImageRepository.class);
    private ReviewPlayTogetherRepository mockReviewPlayTogetherRepository = Mockito.mock(ReviewPlayTogetherRepository.class);
    private ReviewDetailRepository mockReviewDetailRepository = Mockito.mock(ReviewDetailRepository.class);

    private ReviewService reviewService = new ReviewService(
            mockReviewRepository,
            mockReviewQueryRepository,
            mockMemberRepository,
            mockMemberPlayInclinationRepository,
            mockMemberPlayInclinationQueryRepository,
            mockMemberFriendQueryRepository,
            mockThemeRepository,
            mockGenreRepository,
            mockReviewProperties,
            mockReviewPerceivedThemeGenreRepository,
            mockReviewImageRepository,
            mockReviewPlayTogetherRepository,
            mockReviewDetailRepository
    );

    @Test
    void test() {
//        Long memberId = 1L;
//        Long themeId = 1L;
//        Optional<Member> member = Optional.ofNullable(Member.builder()
//                .build());
//        Optional<Theme> theme = Optional.ofNullable(Theme.builder().build());
//        ReviewCreateDto reviewCreateDto = ReviewCreateDto.builder()
//                .clearYN(false)
//                .clearTime(null)
//                .hintUsageCount(ReviewHintUsageCount.THREE_OR_MORE)
//                .rating(4)
//                .friendIds(null)
//                .build();
//
//        given(mockMemberRepository.findById(memberId)).willReturn(member);
//        given(mockThemeRepository.findById(themeId)).willReturn(theme);
//        given(mockReviewRepository.save(any())).willReturn(review);
//
//        Long reviewId = reviewService.createReview(memberId, themeId, reviewCreateDto);

    }
}
