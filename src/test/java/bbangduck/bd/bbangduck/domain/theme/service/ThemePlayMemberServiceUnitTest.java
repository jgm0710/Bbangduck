package bbangduck.bd.bbangduck.domain.theme.service;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemePlayMember;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemePlayMemberQueryRepository;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemePlayMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

class ThemePlayMemberServiceUnitTest {

    ThemePlayMemberRepository themePlayMemberRepository = Mockito.mock(ThemePlayMemberRepository.class);
    ThemePlayMemberQueryRepository themePlayMemberQueryRepository = Mockito.mock(ThemePlayMemberQueryRepository.class);

    ThemePlayMemberService themePlayMemberService = new ThemePlayMemberService(
            themePlayMemberRepository,
            themePlayMemberQueryRepository
    );

    @Test
    @DisplayName("테마 플레이 내역 저장 - 기존에 테마를 플레이하지 했던 회원일 경우")
    public void playTheme_AlreadyPlay() {
        //given
        Member member = Member.builder()
                .id(1L)
                .build();

        Theme theme = Theme.builder()
                .id(1L)
                .build();

        LocalDateTime oldLastPlayTime = LocalDateTime.now().minusDays(1);
        ThemePlayMember themePlayMember = ThemePlayMember.builder()
                .theme(theme)
                .member(member)
                .lastPlayDateTime(oldLastPlayTime)
                .build();

        given(themePlayMemberRepository.findByThemeAndMember(theme, member)).willReturn(Optional.of(themePlayMember));

        //when
        themePlayMemberService.playTheme(theme, member);

        //then
        int dayOfMonth = themePlayMember.getLastPlayDateTime().getDayOfMonth();
        Assertions.assertEquals(LocalDateTime.now().getDayOfMonth(), dayOfMonth);
    }

}