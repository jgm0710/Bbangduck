package bbangduck.bd.bbangduck.domain.theme.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 테마의 유형을 표현한 Enum Type
 *
 * @author jgm
 */
@RequiredArgsConstructor
@Getter
public enum ThemeType {
    DEVICE("장치형"),
    PROBLEM("문제형"),
    HALF("반반");

    private final String description;

    public static List<String> getNameList() {
        return Stream.of(ThemeType.values()).map(Enum::name).collect(Collectors.toList());
    }
}
