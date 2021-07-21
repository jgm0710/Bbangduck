package bbangduck.bd.bbangduck.domain.model.emumerate;

import bbangduck.bd.bbangduck.global.common.EnumType;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public enum NumberOfPeople implements EnumType {
    ONE("한 사람"),
    TWO("두 사람"),
    THREE("세 사람"),
    FOUR("네 사람"),
    FIVE("다섯 사람");

    private final String description;

    @Override
    public String getDescription() {
        return description;
    }
}
