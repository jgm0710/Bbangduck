package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.follow.exception.NotTwoWayFollowRelationException;
import bbangduck.bd.bbangduck.domain.follow.service.FollowService;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.service.MemberPlayInclinationService;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.review.dto.controller.response.*;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewDetailCreateDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewSurveyCreateDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewUpdateDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewSurvey;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemePlayMember;
import bbangduck.bd.bbangduck.domain.theme.service.ThemeAnalysisService;
import bbangduck.bd.bbangduck.domain.theme.service.ThemePlayMemberService;
import bbangduck.bd.bbangduck.domain.theme.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static bbangduck.bd.bbangduck.global.common.NullCheckUtils.existsList;
import static bbangduck.bd.bbangduck.global.common.NullCheckUtils.isNotNull;

/**
 * 리뷰와 관련된 여러 비즈니스 로직을 통합하여 한 단계 상위 계층의 Service Layer 로 구성
 *
 * @author jgm
 */
@Service
@RequiredArgsConstructor
public class ReviewApplicationService {

    private final MemberService memberService;
    private final MemberPlayInclinationService memberPlayInclinationService;

    private final ReviewService reviewService;
    private final ReviewLikeService reviewLikeService;

    private final ThemeService themeService;
    private final ThemeAnalysisService themeAnalysisService;
    private final ThemePlayMemberService themePlayMemberService;

    private final FollowService followService;



    @Transactional
    public Long createReview(Long memberId, Long themeId, ReviewCreateDto reviewCreateDto) {
        Member member = memberService.getMember(memberId);
        Theme theme = themeService.getTheme(themeId);

        List<Member> twoWayFollowMembers = getTwoWayFollowMembers(memberId, reviewCreateDto.getFriendIds());

        Long reviewId = reviewService.saveReview(member, theme, reviewCreateDto);
        Review review = reviewService.getReview(reviewId);

        reviewService.addPlayTogetherFriendsToReview(review, twoWayFollowMembers);

        memberPlayInclinationService.reflectingPropensityOfMemberToPlay(member, theme.getGenre());

        themeService.increaseThemeRating(theme, reviewCreateDto.getRating());

        themePlayMemberService.playTheme(theme, member);

        return reviewId;
    }

    private List<Member> getTwoWayFollowMembers(Long followingMemberId, List<Long> followedMemberIds) {
        if (existsList(followedMemberIds)) {
            boolean isTwoWayFollowMembers = followService.isTwoWayFollowRelationMembers(followingMemberId, followedMemberIds);
            if (isTwoWayFollowMembers) {
                return memberService.getMembers(followedMemberIds);
            } else {
                followedMemberIds.forEach(followedMemberId -> {
                    boolean isTwoWayFollowMember = followService.isTwoWayFollowRelation(followingMemberId, followedMemberId);
                    if (!isTwoWayFollowMember) {
                        throw new NotTwoWayFollowRelationException(followingMemberId, followedMemberId);
                    }
                });
            }
        }
        return null;
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

        themeAnalysisService.reflectingThemeAnalyses(review.getTheme(), reviewSurveyCreateDto.getPerceivedThemeGenres());
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
        List<Member> twoWayFollowMembers = getTwoWayFollowMembers(reviewMember.getId(), reviewUpdateDto.getFriendIds());
        reviewService.addPlayTogetherFriendsToReview(review, twoWayFollowMembers);
        if (reviewUpdateDto.getReviewType() == ReviewType.DETAIL) {
            reviewService.addDetailToReview(review, reviewUpdateDto.toReviewDetailCreateDto());
        }
    }

    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(Long reviewId, Long authenticatedMemberId) {
        Review review = reviewService.getReview(reviewId);
        Member authenticatedMember = memberService.getMember(authenticatedMemberId);
        boolean existsReviewLike = reviewLikeService.getExistsReviewLike(authenticatedMemberId, reviewId);
        boolean possibleOfAddReviewSurvey = reviewService.isPossibleOfAddReviewSurvey(review.getRegisterTimes());
        return convertReviewToResponseDto(review, authenticatedMember, existsReviewLike, possibleOfAddReviewSurvey);
    }

    private ReviewResponseDto convertReviewToResponseDto(Review review, Member currentMember, boolean existsReviewLike, boolean possibleOfAddReviewSurvey) {
        ReviewSurvey reviewSurvey = review.getReviewSurvey();

        switch (review.getReviewType()) {
            case BASE:
                return isNotNull(reviewSurvey) ?
                        SimpleAndSurveyReviewResponseDto.convert(review, currentMember, existsReviewLike, possibleOfAddReviewSurvey) :
                        SimpleReviewResponseDto.convert(review, currentMember, existsReviewLike, possibleOfAddReviewSurvey);
            case DETAIL:
                return isNotNull(reviewSurvey) ?
                        DetailAndSurveyReviewResponseDto.convert(review, currentMember, existsReviewLike, possibleOfAddReviewSurvey) :
                        DetailReviewResponseDto.convert(review, currentMember, existsReviewLike, possibleOfAddReviewSurvey);
            default:
                return null;
        }
    }

    @Transactional
    public void addLikeToReview(Long memberId, Long reviewId) {
        Member member = memberService.getMember(memberId);
        Review review = reviewService.getReview(reviewId);

        reviewLikeService.addLikeToReview(member, review);

        Theme reviewTheme = review.getTheme();
        Member reviewMember = review.getMember();
        ThemePlayMember themePlayMember = themePlayMemberService.getThemePlayMember(reviewTheme.getId(), reviewMember.getId());
        themePlayMember.increaseReviewLikeCount();
    }
}
