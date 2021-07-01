package bbangduck.bd.bbangduck.domain.theme.controller;

import bbangduck.bd.bbangduck.common.BaseControllerTest;
import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.NumberOfPeople;
import bbangduck.bd.bbangduck.domain.review.dto.controller.response.PaginationResponseDto;
import bbangduck.bd.bbangduck.domain.shop.entity.Franchise;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemeImage;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeRatingFilteringType;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeSortCondition;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeType;
import bbangduck.bd.bbangduck.domain.theme.service.ThemeService;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import com.querydsl.core.QueryResults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("테마 API 관련 테스트")
class ThemeApiControllerTest extends BaseControllerTest {

    @MockBean
    ThemeService themeService;


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
        given(themeService.getThemeList(any(), any())).willReturn(themeQueryResults);

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
                                fieldWithPath("list[0].themeId").description("조회된 테마의 식별 ID"),
                                fieldWithPath("list[0].themeImage.themeImageId").description("조회된 테마에 등록된 이미지의 식별 ID"),
                                fieldWithPath("list[0].themeImage.themeImageUrl").description("조회된 테마에 등록된 이미지 파일의 다운로드 URL"),
                                fieldWithPath("list[0].themeImage.themeImageThumbnailUrl").description("조회된 테마에 등록된 이미지 파일의 썸네일 이미지 파일 다운로드 URL"),
                                fieldWithPath("list[0].themeName").description("조회된 테마의 이름"),
                                fieldWithPath("list[0].franchiseName").description("조회된 테마의 프렌차이즈 이름"),
                                fieldWithPath("list[0].shopName").description("조회된 테마의 지점명"),
                                fieldWithPath("nowPageNum").description("현재 조회한 페이지"),
                                fieldWithPath("amount").description("조회된 수량"),
                                fieldWithPath("totalPagesCount").description("요청 시 기입한 조건으로 조회된 결과의 총 페이지 수"),
                                fieldWithPath("prevPageUrl").description("이전 페이지를 요청할 수 있는 URL 정보"),
                                fieldWithPath("nextPageUrl").description("다음 페이지를 요청할 수 있는 URL 정보")
                        )
                ))
        ;

        //given
        MvcResult mvcResult = perform.andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        PaginationResponseDto paginationResponseDto = objectMapper.readValue(contentAsString, PaginationResponseDto.class);
        String nextPageUrl = paginationResponseDto.getNextPageUrl();
        String nextPathContextPath = nextPageUrl.substring(nextPageUrl.indexOf("/api"));

        //when&then

        mockMvc.perform(get(nextPathContextPath))
                .andDo(print())
                .andExpect(status().isOk());
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
        given(themeService.getThemeList(any(), any())).willReturn(themeQueryResults);

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

}