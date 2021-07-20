package bbangduck.bd.bbangduck.api.controller;

import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRoomEscapeRecodesOpenStatus;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberSearchKeywordType;
import bbangduck.bd.bbangduck.domain.member.enumerate.SocialType;
import bbangduck.bd.bbangduck.domain.model.emumerate.*;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewHintUsageCount;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewSearchType;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewSortCondition;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeRatingFilteringType;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeSortCondition;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeType;
import bbangduck.bd.bbangduck.global.common.EnumType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link bbangduck.bd.bbangduck.api.document.enumerate.EnumDocumentationTest} 에서 사용
 * <p>
 * {@link EnumType} 을 구현하여 문서화 해아하는 Enum 값을 {@link EnumDocument} 에 문서화하기 위한 형태로 변환하기 위한 Controller
 *
 * @author Gumin Jeong
 * @since 2021-07-20
 */
@RestController
public class EnumViewController {

    @GetMapping("/docs")
    public EnumDocument findAll() {
        return EnumDocument.testBuilder()
                .memberRoles(getDocs(MemberRole.values()))
                .activities(getDocs(Activity.values()))
                .difficulties(getDocs(Difficulty.values()))
                .numberOfPeoples(getDocs(NumberOfPeople.values()))
                .horrorGrades(getDocs(HorrorGrade.values()))
                .satisfactions(getDocs(Satisfaction.values()))
                .themeTypes(getDocs(ThemeType.values()))
                .themeRatingFilteringTypes(getDocs(ThemeRatingFilteringType.values()))
                .themeSortConditions(getDocs(ThemeSortCondition.values()))
                .memberRoomEscapeRecodesOpenStatus(getDocs(MemberRoomEscapeRecodesOpenStatus.values()))
                .memberSearchKeywordTypes(getDocs(MemberSearchKeywordType.values()))
                .socialTypes(getDocs(SocialType.values()))
                .reviewHintUsageCounts(getDocs(ReviewHintUsageCount.values()))
                .reviewSearchTypes(getDocs(ReviewSearchType.values()))
                .reviewSortConditions(getDocs(ReviewSortCondition.values()))
                .reviewTypes(getDocs(ReviewType.values()))
                .build();
    }

    private Map<String, String> getDocs(EnumType[] enumTypes) {
        return Stream.of(enumTypes).collect(Collectors.toMap(EnumType::name, EnumType::getDescription));
    }
}
