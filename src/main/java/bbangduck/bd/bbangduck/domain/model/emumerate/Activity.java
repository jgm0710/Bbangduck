package bbangduck.bd.bbangduck.domain.model.emumerate;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public enum Activity {
    LITTLE_ACTIVITY("조금 활동"),
    NORMAL("보통"),
    VERY_ACTIVITY("매우 활동");

    private final String description;

    public static List<String> getNameList() {
        return Stream.of(Activity.values()).map(Enum::name).collect(Collectors.toList());
    }
}
