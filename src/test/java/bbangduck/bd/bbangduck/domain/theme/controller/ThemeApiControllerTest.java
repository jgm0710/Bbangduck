package bbangduck.bd.bbangduck.domain.theme.controller;

import bbangduck.bd.bbangduck.common.BaseControllerTest;
import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberProfileImage;
import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.NumberOfPeople;
import bbangduck.bd.bbangduck.domain.shop.entity.Area;
import bbangduck.bd.bbangduck.domain.shop.entity.Franchise;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.theme.dto.controller.request.ThemeGetPlayMemberListRequestDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemeAnalysis;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemeImage;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeGetMemberListSortCondition;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeRatingFilteringType;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeSortCondition;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeType;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeAnalysisQueryRepository;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeQueryRepository;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import com.querydsl.core.QueryResults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("테마 API 관련 테스트")
class ThemeApiControllerTest extends BaseControllerTest {

    @MockBean
    ThemeQueryRepository themeQueryRepository;

    @MockBean
    ThemeRepository themeRepository;

    @MockBean
    ThemeAnalysisQueryRepository themeAnalysisQueryRepository;


    @Test
    @DisplayName("테마 목록 조회")
    public void getThemeList() throws Exception {
        //given

        List<Theme> themeList = new ArrayList<>();
        for (long i = 1; i < 6; i++) {

            Franchise franchise = Franchise.builder()
                    .name("franchiseName" + i)
                    .build();

            Shop shop = Shop.builder()
                    .name("shopName" + i)
                    .franchise(franchise)
                    .build();

            Theme theme = Theme.builder()
                    .id(i)
                    .name("theme" + i)
                    .shop(shop)
                    .build();
            ThemeImage themeImage = ThemeImage.builder()
                    .id(i)
                    .fileStorageId(i)
                    .fileName(UUID.randomUUID() + "sampleImageFile" + i)
                    .build();
            theme.setThemeImage(themeImage);

            themeList.add(theme);
        }

        QueryResults<Theme> themeQueryResults = new QueryResults<>(themeList, 1L, 1L, 30);
        given(themeQueryRepository.findList(any(), any())).willReturn(themeQueryResults);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/themes")
                        .param("pageNum", "2")
                        .param("amount", "5")
                        .param("genreCode", "GR1")
                        .param("themeType", ThemeType.HALF.name())
                        .param("rating", ThemeRatingFilteringType.TWO_OR_MORE.name())
                        .param("numberOfPeople", NumberOfPeople.TWO.name())
                        .param("difficulty", Difficulty.NORMAL.name())
                        .param("activity", Activity.NORMAL.name())
                        .param("horrorGrade", HorrorGrade.NORMAL.name())
                        .param("sortCondition", ThemeSortCondition.RATING_DESC.name())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document(
                        "get-theme-list-success",
                        requestParameters(
                                parameterWithName("pageNum").description("조회할 페이지 기입"),
                                parameterWithName("amount").description("조회할 수량 기입"),
                                parameterWithName("genreCode").description("어떤 장르의 테마를 조회할 것인지 기입 +\n" +
                                        "장르 코드는 장르 목록 조회 API 를 별도로 제공"),
                                parameterWithName("themeType").description("어떤 유형의 테마를 조회할 것인지 기입 +\n" +
                                        ThemeType.getNameList()),
                                parameterWithName("rating").description("몇 점 이상의 평점을 가진 테마를 조회할 것인지 기입 +\n" +
                                        ThemeRatingFilteringType.getNameList()),
                                parameterWithName("numberOfPeople").description("적정 인원이 N 명인 테마 목록을 조회하기 위해 기입 +\n" +
                                        NumberOfPeople.getNameList()),
                                parameterWithName("difficulty").description("어떤 난이도의 테마를 조회할 것인지 기입 +\n" +
                                        Difficulty.getNameList()),
                                parameterWithName("activity").description("활동성 정도 필터링 조건 기입 +\n" +
                                        Activity.getNameList()),
                                parameterWithName("horrorGrade").description("공포도 정도 필터링 조건 기입 +\n" +
                                        HorrorGrade.getNameList()),
                                parameterWithName("sortCondition").description("테마 목록 정렬 조건 기입 +\n" +
                                        ThemeSortCondition.getNameList())
                        ),
                        responseFields(
                                fieldWithPath("contents").description("조회 결과 목록"),
                                fieldWithPath("contents[].themeId").description("조회된 테마의 식별 ID"),
                                fieldWithPath("contents[].themeImage.themeImageId").description("조회된 테마에 등록된 이미지의 식별 ID"),
                                fieldWithPath("contents[].themeImage.themeImageUrl").description("조회된 테마에 등록된 이미지 파일의 다운로드 URL"),
                                fieldWithPath("contents[].themeImage.themeImageThumbnailUrl").description("조회된 테마에 등록된 이미지 파일의 썸네일 이미지 파일 다운로드 URL"),
                                fieldWithPath("contents[].themeName").description("조회된 테마의 이름"),
                                fieldWithPath("contents[].franchiseName").description("조회된 테마의 프렌차이즈 이름"),
                                fieldWithPath("contents[].shopName").description("조회된 테마의 지점명"),
                                fieldWithPath("nowPageNum").description("현재 조회한 페이지"),
                                fieldWithPath("requestAmount").description("조회된 수량"),
                                fieldWithPath("totalResultsCount").description("요청 시 기입한 조건으로 조회된 결과의 총 개수")
                        )
                ))
        ;

    }

    @ParameterizedTest
    @MethodSource("paramsForGetThemeListNotValid")
    @DisplayName("테마 목록 조회 - 조회 페이징 조건이 잘못된 경우")
    public void getThemeList_NotValid(String pageNum, String amount) throws Exception {
        //given
        List<Theme> themes = new ArrayList<>();
        for (long i = 0; i < 5; i++) {
            Theme theme = Theme.builder()
                    .id(i)
                    .build();
            themes.add(theme);
        }

        QueryResults<Theme> themeQueryResults = new QueryResults<>(themes, 1L, 1L, 1);
        given(themeQueryRepository.findList(any(), any())).willReturn(themeQueryResults);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/themes")
                        .param("pageNum", pageNum)
                        .param("amount", amount)
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.GET_THEME_LIST_NOT_VALID.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.GET_THEME_LIST_NOT_VALID.getMessage()));
    }

    private static Stream<Arguments> paramsForGetThemeListNotValid() {
        return Stream.of(
                Arguments.of("0", "10"),
                Arguments.of("1", "0"),
                Arguments.of("1", "501")
        );
    }

    @Test
    @DisplayName("테마 조회")
    public void getTheme() throws Exception {
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
                .playTime(LocalTime.of(1, 0))
                .numberOfPeoples(List.of(NumberOfPeople.ONE, NumberOfPeople.TWO))
                .difficulty(Difficulty.NORMAL)
                .activity(Activity.NORMAL)
                .horrorGrade(HorrorGrade.NORMAL)
                .shop(shop)
                .build();

        ThemeImage themeImage = ThemeImage.builder()
                .id(1L)
                .fileStorageId(132173L)
                .fileName(UUID.randomUUID() + "fileName")
                .build();

        theme.setThemeImage(themeImage);

        Genre genre1 = Genre.builder()
                .id(1L)
                .code("GR1")
                .name("genre1")
                .build();

        Genre genre2 = Genre.builder()
                .id(2L)
                .code("GR2")
                .name("genre2")
                .build();
        theme.addGenre(genre1);
        theme.addGenre(genre2);

        given(themeRepository.findById(theme.getId())).willReturn(Optional.of(theme));

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/themes/{themeId}", theme.getId())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document(
                        "get-theme-success",
                        responseFields(
                                fieldWithPath("themeId").description("조회된 테마의 식별 ID"),
                                fieldWithPath("themeImage").description("조회된 테마에 등록된 이미지 정보"),
                                fieldWithPath("themeImage.themeImageId").description("조회된 테마의 이미지의 식별 ID"),
                                fieldWithPath("themeImage.themeImageUrl").description("조회된 테마의 이미지 다운로드 URL"),
                                fieldWithPath("themeImage.themeImageThumbnailUrl").description("조회된 테마의 이미지의 썸네일 이미지 다운로드 URL"),
                                fieldWithPath("themeName").description("조회된 테마의 이름"),
                                fieldWithPath("themeDescription").description("조회된 테마에 대한 설명"),
                                fieldWithPath("themeRating").description("조회된 테마의 평점"),
                                fieldWithPath("themeGenres").description("조회된 테마의 장르 정보들"),
                                fieldWithPath("themeGenres[].genreId").description("조회된 테마의 장르의 식별 ID"),
                                fieldWithPath("themeGenres[].genreCode").description("조회된 테마의 장르의 코드 값"),
                                fieldWithPath("themeGenres[].genreName").description("조회된 테마의 장르의 이름"),
                                fieldWithPath("shopInfo").description("조회된 테마의 샵 정보"),
                                fieldWithPath("shopInfo.franchiseInfo").description("조회된 테마의 샵의 프랜차이즈 정보"),
                                fieldWithPath("shopInfo.franchiseInfo.franchiseId").description("조회된 테마의 샵의 프렌차이즈의 식별 ID"),
                                fieldWithPath("shopInfo.franchiseInfo.franchiseName").description("조회된 테마의 샵의 프렌차이즈의 이름"),
                                fieldWithPath("shopInfo.shopId").description("조회된 테마의 샵의 식별 ID"),
                                fieldWithPath("shopInfo.shopName").description("조회된 테마의 샵의 이름"),
                                fieldWithPath("shopInfo.areaInfo").description("조회된 테마의 샵의 지역 정보"),
                                fieldWithPath("shopInfo.areaInfo.areaId").description("조회된 테마의 샵의 지역의 식별 ID"),
                                fieldWithPath("shopInfo.areaInfo.areaCode").description("조회된 테마의 샵의 지역의 코드 값"),
                                fieldWithPath("shopInfo.areaInfo.areaName").description("조회된 테마의 샵의 지역의 이름"),
                                fieldWithPath("playTime").description("조회된 테마의 플레이 시간"),
                                fieldWithPath("numberOfPeoples").description("조회된 테마의 참여 가능 인원 수 목록 +\n" +
                                        NumberOfPeople.getNameList()),
                                fieldWithPath("difficulty").description("조회된 테마의 난이도 +\n" +
                                        Difficulty.getNameList()),
                                fieldWithPath("activity").description("조회된 테마의 활동성 +\n" +
                                        Activity.getNameList()),
                                fieldWithPath("horrorGrade").description("조회된 테마의 공포도 +\n" +
                                        HorrorGrade.getNameList())
                        )
                ))
        ;

    }

    @Test
    @DisplayName("테마 조회 - 테마를 찾을 수 없는 경우")
    public void getTheme_NotFound() throws Exception {
        //given
        Long themeId = 1L;
        given(themeRepository.findById(themeId)).willReturn(Optional.empty());

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/themes/{themeId}", themeId)
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value(ResponseStatus.THEME_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.THEME_NOT_FOUND.getMessage()));

    }

    @Test
    @DisplayName("테마 조회 - 삭제된 테마일 경우")
    public void getTheme_DeletedTheme() throws Exception {
        //given
        Theme theme = Theme.builder()
                .id(1L)
                .deleteYN(true)
                .build();

        given(themeRepository.findById(theme.getId())).willReturn(Optional.of(theme));

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/themes/{themeId}", theme.getId())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.MANIPULATE_DELETED_THEME.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.MANIPULATE_DELETED_THEME.getMessage()));

    }

    @Test
    @DisplayName("테마 분석 조회")
    public void getThemeAnalyses() throws Exception {
        //given

        Theme theme = Theme.builder()
                .id(1L)
                .deleteYN(false)
                .build();

        List<ThemeAnalysis> themeAnalyses = new ArrayList<>();
        for (long i = 1; i < 6; i++) {
            Genre genre = Genre.builder()
                    .id(i)
                    .code("GR" + i)
                    .name("genreName" + i)
                    .build();

            ThemeAnalysis themeAnalysis = ThemeAnalysis.builder()
                    .genre(genre)
                    .evaluatedCount((long) new Random().nextInt(4) + 1)
                    .build();
            themeAnalyses.add(themeAnalysis);
        }

        themeAnalyses.sort((o1, o2) -> {
            Long evaluatedCount1 = o1.getEvaluatedCount();
            Long evaluatedCount2 = o2.getEvaluatedCount();

            if (evaluatedCount1 > evaluatedCount2) {
                return -1;
            } else if (evaluatedCount1.equals(evaluatedCount2)) {
                Genre o1Genre = o1.getGenre();
                Genre o2Genre = o2.getGenre();

                int genreCompare = o1Genre.getName().compareTo(o2Genre.getName());
                if (genreCompare > -1) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                return 1;
            }
        });

        given(themeRepository.findById(theme.getId())).willReturn(Optional.of(theme));
        given(themeAnalysisQueryRepository.findByThemeId(theme.getId())).willReturn(themeAnalyses);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/themes/{themeId}/analyses", theme.getId())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document(
                        "get-theme-analyses-success",
                        responseFields(
                                fieldWithPath("[0].genre.genreId").description("테마 분석의 장르의 식별 ID"),
                                fieldWithPath("[0].genre.genreCode").description("테마 분석의 장르의 코드 값"),
                                fieldWithPath("[0].genre.genreName").description("테마 분석의 장르의 이름"),
                                fieldWithPath("[0].evaluatedCount").description("테마 분석의 해당 장르로 평가된 횟수")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("테마 분석 조회 - 테마를 찾을 수 없는 경우")
    public void getThemeAnalyses_NotFound() throws Exception {
        //given
        Long themeId = 1L;
        given(themeRepository.findById(themeId)).willReturn(Optional.empty());

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/themes/{themeId}/analyses", themeId)
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value(ResponseStatus.THEME_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.THEME_NOT_FOUND.getMessage()));

    }

    @Test
    @DisplayName("테마 분석 조회 - 삭제된 테마일 경우")
    public void getThemeAnalyses_DeletedTheme() throws Exception {
        //given
        Theme theme = Theme.builder()
                .id(1L)
                .deleteYN(true)
                .build();

        given(themeRepository.findById(theme.getId())).willReturn(Optional.of(theme));

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/themes/{themeId}/analyses", theme.getId())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.MANIPULATE_DELETED_THEME.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.MANIPULATE_DELETED_THEME.getMessage()));

    }

    @Test
    @DisplayName("테마 플레이 회원 목록 조회")
    public void getThemePlayMemberList() throws Exception {
        //given
        Long themeId = 1L;
        Theme theme = Theme.builder()
                .id(themeId)
                .build();

        List<Member> themePlayMemberList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            long randomLong = new Random().nextInt(50);
            Member member = Member.builder()
                    .id(randomLong)
                    .nickname("member" + randomLong)
                    .build();

            long randomLong2 = new Random().nextInt(50);
            MemberProfileImage profileImage = MemberProfileImage.builder()
                    .id(randomLong)
                    .fileStorageId(randomLong2)
                    .fileName(UUID.randomUUID() + "fileName" + randomLong2)
                    .build();
            member.setProfileImage(profileImage);

            themePlayMemberList.add(member);
        }

        ThemeGetPlayMemberListRequestDto themeGetPlayMemberListRequestDto = ThemeGetPlayMemberListRequestDto.builder()
                .amount(5)
                .sortCondition(ThemeGetMemberListSortCondition.REVIEW_LIKE_COUNT_DESC)
                .build();

        given(themeRepository.findById(themeId)).willReturn(Optional.of(theme));
        given(themeQueryRepository.findThemePlayMemberList(any(), any())).willReturn(themePlayMemberList);
        given(themeQueryRepository.getThemePlayMembersCount(themeId)).willReturn(74L);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/themes/{themeId}/members", themeId)
                        .param("amount", String.valueOf(themeGetPlayMemberListRequestDto.getAmount()))
                        .param("sortCondition", String.valueOf(themeGetPlayMemberListRequestDto.getSortCondition()))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document(
                        "get-theme-play-member-list-success",
                        requestParameters(
                                parameterWithName("amount").description("몇 명의 회원을 조회할 것인지 기입 +\n" +
                                        "기입하지 않을 경우 기본 3"),
                                parameterWithName("sortCondition").description("회원 목록 조회 시 어떤 기준으로 조회할 것인지 기입 +\n" +
                                        "기본은 리뷰에 좋아요를 많이 받은 회원 순으로 내림차순 정렬 +\n" +
                                        ThemeGetMemberListSortCondition.getNameList())
                        ),
                        responseFields(
                                fieldWithPath("membersInfo").description("조회된 회원 목록의 간단한 정보"),
                                fieldWithPath("membersInfo[].memberId").description("조회된 회원의 식별 ID"),
                                fieldWithPath("membersInfo[].nickname").description("조회된 회원의 닉네임"),
                                fieldWithPath("membersInfo[].profileImage.profileImageId").description("조회된 회원의 프로필 이미지 식별 ID"),
                                fieldWithPath("membersInfo[].profileImage.profileImageUrl").description("조회된 회원의 프로필 이미지 다운로드 URL"),
                                fieldWithPath("membersInfo[].profileImage.profileImageThumbnailUrl").description("조회된 회원의 프로필 이미지 썸네일 이미지 다운로드 URL"),
                                fieldWithPath("requestAmount").description("요청된 회원 목록 수량"),
                                fieldWithPath("themePlayMembersCount").description("테마를 플레이한 전체 회원 수")
                        )
                ))
        ;
    }

}