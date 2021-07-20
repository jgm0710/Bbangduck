package bbangduck.bd.bbangduck.domain.theme.enumerate;

import bbangduck.bd.bbangduck.global.common.EnumType;
import lombok.RequiredArgsConstructor;

/**
 * 테마 목록 조회 시 정렬 조건을 지정하기 위한 Enum Type
 *
 * @author jgm
 */
@RequiredArgsConstructor
public enum ThemeSortCondition implements EnumType {
    LATEST("최신 순"),
    OLDEST("오래된 순"),
    RATING_DESC("평점 높은 순"),
    RATING_ASC("평점 낮은 순");

    private final String description;

    @Override
    public String getDescription() {
        return description;
    }
}
