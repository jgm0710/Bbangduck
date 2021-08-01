package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewRepository;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemePlayMember;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemePlayMemberRepository;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ReviewApplicationServiceIntegrationTest extends BaseTest {


    @Autowired
    ReviewApplicationService reviewApplicationService;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    ThemePlayMemberRepository themePlayMemberRepository;

    @AfterEach
    void tearDown() {
        themePlayMemberRepository.deleteAll();
        reviewRepository.deleteAll();
        themeRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("리뷰 삭제 - 회원이 테마에 리뷰를 작성한 내역이 없을 경우 테마 플레이 내역이 제대로 삭제되는지 확인")
    public void deleteReview() {
        //given
        Member member = Member.builder().build();
        Theme theme = Theme.builder().build();
        ThemePlayMember themePlayMember = ThemePlayMember.builder()
                .member(member)
                .theme(theme)
                .reviewLikeCount(200)
                .build();
        Review review = Review.builder()
                .member(member)
                .theme(theme)
                .likeCount(100)
                .build();

        memberRepository.save(member);
        themeRepository.save(theme);
        themePlayMemberRepository.save(themePlayMember);
        reviewRepository.save(review);

        //when
        reviewApplicationService.deleteReview(member.getId(), review.getId());

        //then
        assertTrue(themePlayMemberRepository.findByThemeAndMember(theme, member).isEmpty(), "테마 플래이 내역이 삭제되어 있어야 한다.");
    }

}