package bbangduck.bd.bbangduck.domain.theme.service;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewRepository;
import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemeGetPlayMemberListDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeGetMemberListSortCondition;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DisplayName("ThemeService 통합 테스트")
class ThemeServiceIntegrationTest extends BaseTest {

    @Autowired
    ThemeService themeService;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAll();
        themeRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("테마 플레이 회원 목록 조회 - 중복이 제거 되었는지 확인")
    public void findThemePlayMemberList_Distinct() {
        //given
        Theme theme = Theme.builder().build();

        themeRepository.save(theme);

        Member member1 = Member.builder()
                .nickname("member1")
                .roles(Set.of(MemberRole.USER))
                .build();
        Member member2 = Member.builder()
                .nickname("member2")
                .roles(Set.of(MemberRole.USER))
                .build();
        Member member3 = Member.builder()
                .nickname("member3")
                .roles(Set.of(MemberRole.USER))
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        Review review1 = Review.builder()
                .member(member1)
                .theme(theme)
                .likeCount(4)
                .build();

        Review review2 = Review.builder()
                .member(member2)
                .theme(theme)
                .likeCount(2)
                .build();

        Review review3 = Review.builder()
                .member(member2)
                .theme(theme)
                .likeCount(3)
                .build();

        Review review4 = Review.builder()
                .member(member3)
                .theme(theme)
                .likeCount(3)
                .build();

        Review review5 = Review.builder()
                .member(member3)
                .theme(theme)
                .likeCount(5)
                .build();

        reviewRepository.save(review1);
        reviewRepository.save(review2);
        reviewRepository.save(review3);
        reviewRepository.save(review4);
        reviewRepository.save(review5);

        ThemeGetPlayMemberListDto themeGetPlayMemberListDto = new ThemeGetPlayMemberListDto(4, ThemeGetMemberListSortCondition.REVIEW_LIKE_COUNT_DESC);

        //when
        List<Member> findMembers = themeService.findThemePlayMemberList(theme.getId(), themeGetPlayMemberListDto);

        //then
        assertEquals(3, findMembers.size(),"조회 수량은 4개지만 실제 회원은 3명이므로 List 의 크기는 3이다.");

        assertEquals(member3.getId(), findMembers.get(0).getId(), "처음 조회되는 회원은 좋아요를 5개 받은 member3 이다.");
        assertEquals(member1.getId(), findMembers.get(1).getId(),"두 번째로 조회되는 회원은 좋아요를 4개 받은 member1 이다.");
        // member3, member2 의 리뷰가 좋아요 3개로 같지만 회원 중복을 제거했기 때문에 member2 가 세번째로 조회됨.
        assertEquals(member2.getId(), findMembers.get(2).getId(), "세 번째로 조회되는 회원은 좋아요를 3개 받은 member2 이다.");

    }

}