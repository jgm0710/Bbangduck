package bbangduck.bd.bbangduck.domain.theme.service;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemePlayMember;
import bbangduck.bd.bbangduck.domain.theme.exception.ThemePlayMemberNotFoundException;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemePlayMemberRepository;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ThemePlayMemberServiceIntegrationTest extends BaseTest {

    @Autowired
    ThemePlayMemberService themePlayMemberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    ThemePlayMemberRepository themePlayMemberRepository;

    @AfterEach
    void tearDown() {
        themePlayMemberRepository.deleteAll();
        themeRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("테마 플레이 내역 저장 - 기존에 테마를 플레이하지 않은 회원일 경우")
    public void playTheme_NewPlay() {
        //given
        Member member = Member.builder()
                .build();

        Theme theme = Theme.builder()
                .build();

        memberRepository.save(member);
        themeRepository.save(theme);

        //when
        themePlayMemberService.playTheme(theme, member);

        //then
        ThemePlayMember findThemePlayMember = themePlayMemberRepository.findByThemeAndMember(theme, member).orElseThrow(ThemePlayMemberNotFoundException::new);

        assertNotNull(findThemePlayMember);

        assertEquals(LocalDateTime.now().getYear(), findThemePlayMember.getLastPlayDateTime().getYear());
        assertEquals(LocalDateTime.now().getMonth(), findThemePlayMember.getLastPlayDateTime().getMonth());
        assertEquals(LocalDateTime.now().getDayOfMonth(), findThemePlayMember.getLastPlayDateTime().getDayOfMonth());

        assertEquals(0, findThemePlayMember.getReviewLikeCount());
    }

    @Test
    @DisplayName("테마 플레이 내역 삭제")
    public void deleteThemePlayMember() {
        //given
        Member member = Member.builder().build();
        Theme theme = Theme.builder().build();

        memberRepository.save(member);
        themeRepository.save(theme);

        ThemePlayMember themePlayMember = ThemePlayMember.init(theme, member);

        //when
        themePlayMemberService.deleteThemePlayMember(themePlayMember);

        //then
        assertTrue(themePlayMemberRepository.findByThemeAndMember(theme, member).isEmpty());

    }
}