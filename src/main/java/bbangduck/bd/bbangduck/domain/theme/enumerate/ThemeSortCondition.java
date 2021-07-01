package bbangduck.bd.bbangduck.domain.theme.enumerate;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 테마 목록 조회 시 정렬 조건을 지정하기 위한 Enum Type
 *
 * @author jgm
 */
@RequiredArgsConstructor
public enum ThemeSortCondition {
    LATEST("최신 순"),
    OLDEST("오래된 순"),
    RATING_DESC("평점 높은 순"),
    RATING_ASC("평점 낮은 순");

    private final String description;

    public static List<String> getNameList() {
        return Stream.of(ThemeSortCondition.values()).map(Enum::name).collect(Collectors.toList());
    }

}
