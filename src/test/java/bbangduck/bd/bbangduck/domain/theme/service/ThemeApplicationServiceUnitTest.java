package bbangduck.bd.bbangduck.domain.theme.service;

import bbangduck.bd.bbangduck.domain.genre.Genre;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberProfileImage;
import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.NumberOfPeople;
import bbangduck.bd.bbangduck.domain.shop.entity.Area;
import bbangduck.bd.bbangduck.domain.shop.entity.Franchise;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemeGetPlayMemberListDto;
import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemePlayMemberListResultDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemeAnalysis;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemeImage;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemePlayMember;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeGetMemberListSortCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

class ThemeApplicationServiceUnitTest {


    ThemeService themeMockService = mock(ThemeService.class);
    ThemeAnalysisService themeAnalysisMockService = mock(ThemeAnalysisService.class);
    ThemePlayMemberService themePlayMemberMockService = mock(ThemePlayMemberService.class);

    ThemeApplicationService themeMockApplicationService = new ThemeApplicationService(
            themeMockService,
            themeAnalysisMockService,
            themePlayMemberMockService
    );

    @Test
    @DisplayName("테마 조회")
    public void getTheme() {
        //given
        Area area = Area.builder()
                .id(1L)
                .code("AR1")
                .name("지역1")
                .build();

        Franchise franchise = Franchise.builder()
                .id(1L)
                .name("franchiseName")
                .build();

        Shop shop = Shop.builder()
                .id(1L)
                .name("shopName")
                .franchise(franchise)
                .area(area)
                .build();

        Theme theme = Theme.builder()
                .id(1L)
                .name("themeName")
                .description("theme description")
                .playTime(LocalTime.now())
                .numberOfPeoples(List.of(NumberOfPeople.ONE, NumberOfPeople.TWO))
                .difficulty(Difficulty.NORMAL)
                .activity(Activity.NORMAL)
                .horrorGrade(HorrorGrade.NORMAL)
                .totalRating(60L)
                .totalEvaluatedCount(16L)
                .shop(shop)
                .build();

        ThemeImage themeImage = ThemeImage.builder()
                .id(1L)
                .fileStorageId(132173L)
                .fileName(UUID.randomUUID() + "fileName")
                .build();

        theme.setThemeImage(themeImage);

        given(themeMockService.getTheme(theme.getId())).willReturn(theme);


        //when
        Theme findTheme = themeMockApplicationService.getTheme(theme.getId());

        //then
        then(themeMockService).should(times(1)).getTheme(theme.getId());


    }

    @Test
    @DisplayName("테마 분석 조회")
    public void getThemeAnalyses() {
        //given
        List<ThemeAnalysis> themeAnalyses = new ArrayList<>();

        for (long i = 0; i < 5; i++) {
            Genre genre = Arrays.stream(Genre.values()).findAny().orElseThrow();

            ThemeAnalysis themeAnalysis = ThemeAnalysis.builder()
                    .genre(genre)
                    .evaluatedCount(i)
                    .build();
            themeAnalyses.add(themeAnalysis);
        }

        Long themeId = 1L;
        given(themeAnalysisMockService.getThemeAnalyses(themeId)).willReturn(themeAnalyses);

        //when
        List<ThemeAnalysis> findThemeAnalyses = themeMockApplicationService.getThemeAnalyses(themeId);

        //then
        then(themeMockApplicationService).should(times(1)).getThemeAnalyses(themeId);

    }

    @Test
    @DisplayName("테마를 플레이한 회원 목록 조회")
    public void getThemePlayMemberList() {
        //given
        Long themeId = 1L;
        Theme theme = Theme.builder()
                .id(themeId)
                .build();

        List<ThemePlayMember> themePlayMemberList = new ArrayList<>();
        for (long i = 0; i < 4; i++) {
            Member member = Member.builder()
                    .id(i)
                    .nickname("member" + i)
                    .build();

            long randomLong = new Random().nextInt(100);
            MemberProfileImage profileImage = MemberProfileImage.builder()
                    .id(i)
                    .fileStorageId(randomLong)
                    .fileName(UUID.randomUUID() + "fileName" + randomLong)
                    .build();

            member.setProfileImage(profileImage);

            ThemePlayMember themePlayMember = ThemePlayMember.builder()
                    .theme(theme)
                    .member(member)
                    .build();

            themePlayMemberList.add(themePlayMember);
        }

        ThemeGetPlayMemberListDto themeGetPlayMemberListDto = new ThemeGetPlayMemberListDto(1,10, ThemeGetMemberListSortCondition.REVIEW_LIKE_COUNT_DESC);

        given(themePlayMemberMockService.findThemePlayMemberList(themeId, themeGetPlayMemberListDto)).willReturn(themePlayMemberList);
        given(themePlayMemberMockService.getThemePlayMembersCount(themeId)).willReturn(4L);

        //when
        ThemePlayMemberListResultDto resultDto = themeMockApplicationService.getThemePlayMemberList(themeId, themeGetPlayMemberListDto);

        //then
        then(themeMockService).should(times(1)).getTheme(themeId);
        then(themePlayMemberMockService).should(times(1)).findThemePlayMemberList(themeId, themeGetPlayMemberListDto);
        then(themePlayMemberMockService).should(times(1)).getThemePlayMembersCount(themeId);
    }

}