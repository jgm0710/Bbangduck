package bbangduck.bd.bbangduck.api.document.enumerate;

import bbangduck.bd.bbangduck.api.controller.EnumDocument;
import bbangduck.bd.bbangduck.api.document.utils.CustomResponseFieldsSnippet;
import bbangduck.bd.bbangduck.common.BaseControllerTest;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link bbangduck.bd.bbangduck.global.common.EnumType} 을 구현한 Enum 값들에 대한 문서 조각을 생성하기 위한 Test
 * <p>
 * {@link bbangduck.bd.bbangduck.api.controller.EnumViewController} 를 통해 {@link EnumDocument} 값 초기화
 * <p>
 * mvcResult 의 response body 를 {@link com.fasterxml.jackson.databind.ObjectMapper} 를 통해 {@link EnumDocument} 로 변환
 * <p>
 * 생성된 snippet 을 사용하여 build/generated-snippets/common/ 하위에 문서 생성
 * <p>
 * 각 API Test 에서 {@link bbangduck.bd.bbangduck.api.document.utils.DocumentLinkGenerator} 를 사용하여 문서 링크 지정
 *
 * @author Gumin Jeong
 * @since 2021-07-20
 */
public class EnumDocumentationTest extends BaseControllerTest {

    @Test
    @DisplayName("Enum 값들을 문서화하기 위한 Test")
    public void enumDocument() throws Exception {
        //given

        //when
        ResultActions perform = mockMvc.perform(
                get("/docs")
                        .accept(MediaType.APPLICATION_JSON)
        );

        MvcResult mvcResult = perform.andReturn();
        EnumDocument enumDocument = getData(mvcResult);

        //then
        perform.andExpect(status().isOk())
                .andDo(document(
                        "common",
                        customResponseFields("memberRoles", "회원권한", enumConvertFieldDescriptor(enumDocument.getMemberRoles())),
                        customResponseFields("activities", "활동성", enumConvertFieldDescriptor(enumDocument.getActivities())),
                        customResponseFields("difficulties", "난이도", enumConvertFieldDescriptor(enumDocument.getDifficulties())),
                        customResponseFields("numberOfPeoples", "참여 인원", enumConvertFieldDescriptor(enumDocument.getNumberOfPeoples())),
                        customResponseFields("horrorGrades", "공포도", enumConvertFieldDescriptor(enumDocument.getHorrorGrades())),
                        customResponseFields("satisfactions", "만족도", enumConvertFieldDescriptor(enumDocument.getSatisfactions())),
                        customResponseFields("themeTypes", "테마 유형", enumConvertFieldDescriptor(enumDocument.getThemeTypes())),
                        customResponseFields("themeRatingFilteringTypes", "테마 목록 조회 평점 필터링 조건", enumConvertFieldDescriptor(enumDocument.getThemeRatingFilteringTypes())),
                        customResponseFields("themeSortConditions", "테마 목록 조회 정렬 조건", enumConvertFieldDescriptor(enumDocument.getThemeSortConditions())),
                        customResponseFields("memberRoomEscapeRecodesOpenStatus", "회원 방탈출 공개 상태", enumConvertFieldDescriptor(enumDocument.getMemberRoomEscapeRecodesOpenStatus())),
                        customResponseFields("memberSearchKeywordTypes", "회원 검색 키워드 타입", enumConvertFieldDescriptor(enumDocument.getMemberSearchKeywordTypes())),
                        customResponseFields("socialTypes", "소셜 타입", enumConvertFieldDescriptor(enumDocument.getSocialTypes())),
                        customResponseFields("reviewHintUsageCounts", "리뷰 힌트 사용 개수", enumConvertFieldDescriptor(enumDocument.getReviewHintUsageCounts())),
                        customResponseFields("reviewSearchTypes", "리뷰 목록 조회 조건", enumConvertFieldDescriptor(enumDocument.getReviewSearchTypes())),
                        customResponseFields("reviewSortConditions", "리뷰 목록 조회 정렬 조건", enumConvertFieldDescriptor(enumDocument.getReviewSortConditions())),
                        customResponseFields("reviewTypes", "리뷰 타입", enumConvertFieldDescriptor(enumDocument.getReviewTypes()))
                ));
    }

    /**
     * response 에서 응답 body array 를 가져와서 EnumDocument 로 변환
     */
    private EnumDocument getData(MvcResult mvcResult) throws IOException {
        return objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), new TypeReference<>() {
        });
    }

    /**
     * Map 으로 구성된 enumValues array 를 FieldDescriptor array 로 변환
     */
    private static FieldDescriptor[] enumConvertFieldDescriptor(Map<String, String> enumValues) {
        return enumValues.entrySet().stream()
                .map(sse -> fieldWithPath(sse.getKey()).description(sse.getValue()))
                .toArray(FieldDescriptor[]::new);
    }

    /**
     * /src/test/resources/org.springframework.restdocs.templates/custom-response-fields.snippet 을 사용하여
     * {@link EnumDocument} 에 대한 snippets 생성
     * <p>
     * - documentFieldName : 문서화 할 field name 지정 <br>
     * -> {@link EnumDocument} 의 fieldName 지정
     * <p>
     * - title : custom-response-fields 의 title 위치에 들어갈 값 지정
     * <p>
     * - descriptors : field 에 대한 설명 값 기입 <br>
     * {@link org.springframework.restdocs.payload.PayloadDocumentation#fieldWithPath(String)} 사용
     */
    public static CustomResponseFieldsSnippet customResponseFields(
            String documentFieldName,
            String title,
            FieldDescriptor... descriptors
    ) {
        return CustomResponseFieldsSnippet.builder()
                .type("custom-response")
                .subsectionExtractor(beneathPath(documentFieldName).withSubsectionId(documentFieldName))
                .attributes(attributes(key("title").value(title)))
                .descriptors(Arrays.asList(descriptors))
                .ignoreUndocumentedFields(true)
                .build();
    }
}
