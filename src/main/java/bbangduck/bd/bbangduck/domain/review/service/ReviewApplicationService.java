package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.service.MemberPlayInclinationService;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Long createReview(Long memberId, Long themeId, ReviewCreateDto reviewCreateDto) {
        Member member = memberService.getMember(memberId);
        Theme theme = themeService.getTheme(themeId);
        Long reviewId = reviewService.saveReview(member, theme, reviewCreateDto);
        Review review = reviewService.getReview(reviewId);
        reviewService.addPlayTogetherFriendsToReview(review, reviewCreateDto.getFriendIds());
        memberPlayInclinationService.reflectingPropensityOfMemberToPlay(member, theme.getGenres());
        themeService.reflectThemeRating(theme, reviewCreateDto.getRating());

        return reviewId;
    }
}
