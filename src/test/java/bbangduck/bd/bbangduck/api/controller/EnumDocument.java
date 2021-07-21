package bbangduck.bd.bbangduck.api.controller;

import lombok.*;

import java.util.Map;

/**
 * Enum 값을 문서화 하기 위한 구현한 Util Class
 * <p>
 * {@link EnumViewController#findAll()} 을 통해 값 초기화
 * <p>
 * {@link bbangduck.bd.bbangduck.api.document.enumerate.EnumDocumentationTest} 를 통해 스니펫 생성
 *
 * @author Gumin Jeong
 * @since 2021-07-20
 */
@Getter
@Builder(builderClassName = "TestBuilder", builderMethodName = "testBuilder")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnumDocument {
    Map<String, String> memberRoles;
    Map<String, String> horrorGrades;
    Map<String, String> activities;
    Map<String, String> difficulties;
    Map<String, String> numberOfPeoples;
    Map<String, String> satisfactions;
    Map<String, String> themeTypes;
    Map<String, String> themeRatingFilteringTypes;
    Map<String, String> themeSortConditions;
    Map<String, String> memberRoomEscapeRecodesOpenStatus;
    Map<String, String> memberSearchKeywordTypes;
    Map<String, String> socialTypes;
    Map<String, String> reviewHintUsageCounts;
    Map<String, String> reviewSearchTypes;
    Map<String, String> reviewSortConditions;
    Map<String, String> reviewTypes;


}
