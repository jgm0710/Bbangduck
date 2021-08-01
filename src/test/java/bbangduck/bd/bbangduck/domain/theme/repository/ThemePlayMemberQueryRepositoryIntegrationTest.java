package bbangduck.bd.bbangduck.domain.theme.repository;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemeGetPlayMemberListDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemePlayMember;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeGetMemberListSortCondition;
import bbangduck.bd.bbangduck.domain.theme.exception.ThemePlayMemberNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ThemePlayMemberQueryRepositoryIntegrationTest extends BaseTest {

    @Autowired
    ThemePlayMemberQueryRepository themePlayMemberQueryRepository;

    @Autowired
    ThemePlayMemberRepository themePlayMemberRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ThemeRepository themeRepository;

    @AfterEach
    void tearDown() {
        themePlayMemberRepository.deleteAll();
        themeRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("테마 ID, 회원 ID 를 통한 테마 플레이 내역 단 건 조회")
    public void findThemePlayMemberByThemeIdAndMemberId() {
        //given
        Member user = Member.builder()
                .roles(Set.of(MemberRole.USER))
                .build();
        memberRepository.save(user);

        Theme theme = Theme.builder().build();
        themeRepository.save(theme);

        ThemePlayMember themePlayMember1 = ThemePlayMember.init(theme, user);
        themePlayMemberRepository.save(themePlayMember1);

        //when
        ThemePlayMember findThemePlayMember = themePlayMemberQueryRepository.findByThemeIdAndMemberId(theme.getId(), user.getId()).orElseThrow(ThemePlayMemberNotFoundException::new);

        //then
        assertNotNull(findThemePlayMember);

        assertEquals(user.getId(), findThemePlayMember.getMember().getId());
        assertEquals(theme.getId(), findThemePlayMember.getTheme().getId());

    }

    @Test
    @DisplayName("테마를 플레이한 회원 목록 조회")
    public void findListByThemeId() {
        //given
        Member user1 = Member.builder()
                .roles(Set.of(MemberRole.USER))
                .build();

        Member user2 = Member.builder()
                .roles(Set.of(MemberRole.USER))
                .build();

        Member withdrawal = Member.builder()
                .roles(Set.of(MemberRole.WITHDRAWAL))
                .build();

        Theme theme = Theme.builder().build();

        ThemePlayMember themePlayMember1 = ThemePlayMember.builder()
                .theme(theme)
                .member(user1)
                .reviewLikeCount(new Random().nextInt(100))
                .build();

        ThemePlayMember themePlayMember2 = ThemePlayMember.builder()
                .theme(theme)
                .member(withdrawal)
                .reviewLikeCount(new Random().nextInt(100))
                .build();

        ThemePlayMember themePlayMember3 = ThemePlayMember.builder()
                .theme(theme)
                .member(user2)
                .reviewLikeCount(new Random().nextInt(100))
                .build();

        memberRepository.save(user1);
        memberRepository.save(user2);
        memberRepository.save(withdrawal);
        themeRepository.save(theme);
        themePlayMemberRepository.save(themePlayMember1);
        themePlayMemberRepository.save(themePlayMember2);
        themePlayMemberRepository.save(themePlayMember3);

        ThemeGetPlayMemberListDto themeGetPlayMemberListDto = new ThemeGetPlayMemberListDto(1, 3, ThemeGetMemberListSortCondition.REVIEW_LIKE_COUNT_DESC);

        //when
        List<ThemePlayMember> findResults = themePlayMemberQueryRepository.findListByThemeId(theme.getId(), themeGetPlayMemberListDto);

        //then
        assertEquals(2, findResults.size(), "탈퇴된 회원은 조회되지 않으므로 조회된 개수는 2");

        for (int i = 0; i < findResults.size()-1; i++) {
            ThemePlayMember nowResult = findResults.get(i);
            ThemePlayMember nextResult = findResults.get(i + 1);

            assertTrue(nowResult.getReviewLikeCount() >= nextResult.getReviewLikeCount(), "리뷰 좋아요 개수 많은 순으로 내림차순 정렬");
        }

    }

    @Test
    @DisplayName("테마 플레이 회원 수 조회")
    public void getThemePlayMembersCount() {
        //given
        //given
        Member user1 = Member.builder()
                .roles(Set.of(MemberRole.USER))
                .build();

        Member user2 = Member.builder()
                .roles(Set.of(MemberRole.USER))
                .build();

        Member withdrawal = Member.builder()
                .roles(Set.of(MemberRole.WITHDRAWAL))
                .build();

        Theme theme = Theme.builder().build();

        ThemePlayMember themePlayMember1 = ThemePlayMember.builder()
                .theme(theme)
                .member(user1)
                .reviewLikeCount(new Random().nextInt(100))
                .build();

        ThemePlayMember themePlayMember2 = ThemePlayMember.builder()
                .theme(theme)
                .member(withdrawal)
                .reviewLikeCount(new Random().nextInt(100))
                .build();

        ThemePlayMember themePlayMember3 = ThemePlayMember.builder()
                .theme(theme)
                .member(user2)
                .reviewLikeCount(new Random().nextInt(100))
                .build();

        memberRepository.save(user1);
        memberRepository.save(user2);
        memberRepository.save(withdrawal);
        themeRepository.save(theme);
        themePlayMemberRepository.save(themePlayMember1);
        themePlayMemberRepository.save(themePlayMember2);
        themePlayMemberRepository.save(themePlayMember3);

        //when
        long result = themePlayMemberQueryRepository.getThemePlayMembersCount(theme.getId());

        //then
        assertEquals(2, result, "테마를 플레이한 회원의 총 수는 탈퇴된 회원을 제외하고 총 2명이다.");

    }

}