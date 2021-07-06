package bbangduck.bd.bbangduck.domain.model.emumerate;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public enum NumberOfPeople {
    ONE("한 사람"),
    TWO("두 사람"),
    THREE("세 사람"),
    FOUR("네 사람"),
    FIVE("다섯 사람");

    private final String description;

    public static List<String> getNameList() {
        return Stream.of(NumberOfPeople.values()).map(Enum::name).collect(Collectors.toList());
    }

}
