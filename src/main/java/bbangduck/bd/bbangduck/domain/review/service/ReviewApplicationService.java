package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.friend.service.MemberFriendService;
import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.genre.service.GenreService;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.service.MemberPlayInclinationService;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewDetailCreateDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewSurveyCreateDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewUpdateDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.service.ThemeAnalysisService;
import bbangduck.bd.bbangduck.domain.theme.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 리뷰와 관련된 여러 비즈니스 로직을 통합하여 한 단계 상위 계층의 Service Layer 로 구성
 *
 * @author jgm
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewApplicationService {

    private final ReviewService reviewService;

    private final ThemeService themeService;

    private final MemberService memberService;

    private final MemberPlayInclinationService memberPlayInclinationService;

    private final MemberFriendService memberFriendService;

    private final GenreService genreService;

    private final ThemeAnalysisService themeAnalysisService;

    @Transactional
    public Long createReview(Long memberId, Long themeId, ReviewCreateDto reviewCreateDto) {
        Member member = memberService.getMember(memberId);
        Theme theme = themeService.getTheme(themeId);
        List<Member> acceptedFriends = memberFriendService.getAcceptedFriends(memberId, reviewCreateDto.getFriendIds());

        Long reviewId = reviewService.saveReview(member, theme, reviewCreateDto);
        Review review = reviewService.getReview(reviewId);

        reviewService.addPlayTogetherFriendsToReview(review, acceptedFriends);

        memberPlayInclinationService.reflectingPropensityOfMemberToPlay(member, theme.getGenres());

        themeService.increaseThemeRating(theme, reviewCreateDto.getRating());

        return reviewId;
    }

    @Transactional
    public void addDetailToReview(Long reviewId, Long authenticatedMemberId, ReviewDetailCreateDto reviewDetailCreateDto) {
        Review review = reviewService.getReview(reviewId);

        Member reviewMember = review.getMember();
        reviewService.checkIfMyReview(authenticatedMemberId, reviewMember.getId());

        reviewService.addDetailToReview(review, reviewDetailCreateDto);
    }

    @Transactional
    public void addSurveyToReview(Long reviewId, Long authenticatedMemberId, ReviewSurveyCreateDto reviewSurveyCreateDto) {
        Review review = reviewService.getReview(reviewId);

        Member reviewMember = review.getMember();
        reviewService.checkIfMyReview(authenticatedMemberId, reviewMember.getId());

        reviewService.addSurveyToReview(review, reviewSurveyCreateDto);
        List<Genre> genres = genreService.getGenresByCodes(reviewSurveyCreateDto.getGenreCodes());
        reviewService.addGenresToReviewSurvey(review.getReviewSurvey(), genres);

        themeAnalysisService.reflectingThemeAnalyses(review.getTheme(), genres);
    }

    @Transactional
    public void updateReview(Long reviewId, Long authenticatedMemberId, ReviewUpdateDto reviewUpdateDto) {
        Review review = reviewService.getReview(reviewId);

        Member reviewMember = review.getMember();
        reviewService.checkIfMyReview(authenticatedMemberId, reviewMember.getId());

        themeService.updateThemeRating(review.getTheme(), review.getRating(), reviewUpdateDto.getRating());

        reviewService.clearReviewPlayTogether(review);
        reviewService.clearReviewDetail(review);

        reviewService.updateReviewBase(review, reviewUpdateDto.toReviewUpdateBaseDto());
        List<Member> acceptedFriends = memberFriendService.getAcceptedFriends(reviewMember.getId(), reviewUpdateDto.getFriendIds());
        reviewService.addPlayTogetherFriendsToReview(review, acceptedFriends);
        if (reviewUpdateDto.getReviewType() == ReviewType.DETAIL) {
            reviewService.addDetailToReview(review, reviewUpdateDto.toReviewDetailCreateDto());
        }
    }
}
