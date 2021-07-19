package bbangduck.bd.bbangduck.domain.theme.service;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemeGetPlayMemberListDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeGetMemberListSortCondition;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeQueryRepository;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("ThemeService 단위 테스트")
class ThemeServiceUnitTest {

    ThemeRepository themeRepository = Mockito.mock(ThemeRepository.class);
    ThemeQueryRepository themeQueryRepository = Mockito.mock(ThemeQueryRepository.class);

    ThemeService themeService = new ThemeService(themeRepository, themeQueryRepository);

    @Test
    @DisplayName("테마 레이팅 증가")
    public void increaseThemeRating() {
        //given
        long firstRating = 10L;
        long firstEvaluateCount = 2L;

        Theme theme = Theme.builder()
                .id(1L)
                .totalRating(firstRating)
                .totalEvaluatedCount(firstEvaluateCount)
                .build();

        //when
        int increaseRating = 5;
        themeService.increaseThemeRating(theme, increaseRating);

        //then
        assertEquals(firstRating + increaseRating, theme.getTotalRating());
        assertEquals(firstEvaluateCount + 1, theme.getTotalEvaluatedCount());
    }

    @Test
    @DisplayName("테마 레이팅 감소")
    public void decreaseThemeRating() {
        //given
        long firstRating = 10L;
        long firstEvaluateCount = 2L;

        Theme theme = Theme.builder()
                .id(1L)
                .totalRating(firstRating)
                .totalEvaluatedCount(firstEvaluateCount)
                .build();

        //when
        int decreaseRating = 3;
        themeService.decreaseThemeRating(theme, decreaseRating);

        //then
        assertEquals(firstRating-decreaseRating, theme.getTotalRating());
        assertEquals(firstEvaluateCount - 1, theme.getTotalEvaluatedCount());

    }

    @Test
    @DisplayName("테마 레이팅 업데이트")
    public void updateThemeRating() {
        //given
        long firstRating = 10L;
        long firstEvaluateCount = 2L;

        Theme theme = Theme.builder()
                .id(1L)
                .totalRating(firstRating)
                .totalEvaluatedCount(firstEvaluateCount)
                .build();

        //when
        int decreaseRating = 4;
        int increaseRating = 3;
        themeService.updateThemeRating(theme, decreaseRating, increaseRating);

        //then
        assertEquals(firstRating-decreaseRating+increaseRating, theme.getTotalRating());
        assertEquals(firstEvaluateCount, theme.getTotalEvaluatedCount());
    }

    @Test
    @DisplayName("테마를 플레이한 회원 목록 조회 - 중복되는 회원이 잘 제거되는지 확인")
    public void findThemePlayMemberList_distinct() {
        //given

        List<Member> themePlayMemberList = new ArrayList<>();
        Member member1 = Member.builder()
                .id(1L)
                .build();

        Member member2 = Member.builder()
                .id(1L)
                .roles(Set.of(MemberRole.USER))
                .build();

        Member member3 = Member.builder()
                .id(1L)
                .roles(Set.of(MemberRole.USER))
                .build();

        Member member4 = Member.builder()
                .id(2L)
                .roles(Set.of(MemberRole.USER))
                .build();

        Member member5 = Member.builder()
                .id(2L)
                .roles(Set.of(MemberRole.USER))
                .build();

        themePlayMemberList.add(member1);
        themePlayMemberList.add(member2);
        themePlayMemberList.add(member3);
        themePlayMemberList.add(member4);
        themePlayMemberList.add(member5);

        given(themeQueryRepository.findThemePlayMemberList(any(), any())).willReturn(themePlayMemberList);

        //when
        ThemeGetPlayMemberListDto themeGetPlayMemberListDto = new ThemeGetPlayMemberListDto(3, ThemeGetMemberListSortCondition.REVIEW_LIKE_COUNT_DESC);
        List<Member> findMembers = themeService.findThemePlayMemberList(1L, themeGetPlayMemberListDto);

        //then
        assertEquals(2, findMembers.size(), "ID 를 통해 중복이 제거돼서 List 의 size 는 2 이다.");

    }

}