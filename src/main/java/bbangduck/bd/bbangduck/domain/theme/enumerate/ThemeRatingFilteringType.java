package bbangduck.bd.bbangduck.domain.theme.enumerate;

import bbangduck.bd.bbangduck.global.common.EnumType;
import lombok.RequiredArgsConstructor;

/**
 * 테마 목록 조회 시 평점 몇 점 이상의 테마들만 조회할 것인지 지정하기 위해 지정하기 위한 Enum Type
 *
 * @author jgm
 */
@RequiredArgsConstructor
public enum ThemeRatingFilteringType implements EnumType {
    TWO_OR_MORE("2 점 이상"),
    THREE_OR_MORE("3 점 이상"),
    FOUR_OR_MORE("4 점 이상");

    private final String description;

    @Override
    public String getDescription() {
        return description;
    }
}
