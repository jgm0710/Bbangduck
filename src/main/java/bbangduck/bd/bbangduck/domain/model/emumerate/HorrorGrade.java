package bbangduck.bd.bbangduck.domain.model.emumerate;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public enum HorrorGrade {
    LITTLE_HORROR("조금 공포"),
    NORMAL("보통"),
    VERY_HORROR("매우 공포");

    private final String description;

    public static List<String> getNameList() {
        return Stream.of(HorrorGrade.values()).map(Enum::name).collect(Collectors.toList());
    }
}
