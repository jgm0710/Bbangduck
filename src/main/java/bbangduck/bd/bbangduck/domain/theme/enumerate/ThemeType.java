package bbangduck.bd.bbangduck.domain.theme.enumerate;

import bbangduck.bd.bbangduck.global.common.EnumType;
import lombok.RequiredArgsConstructor;

/**
 * 테마의 유형을 표현한 Enum Type
 *
 * @author jgm
 */
@RequiredArgsConstructor
public enum ThemeType implements EnumType {
    DEVICE("장치형"),
    PROBLEM("문제형"),
    HALF("반반");

    private final String description;

    @Override
    public String getDescription() {
        return description;
    }
}
