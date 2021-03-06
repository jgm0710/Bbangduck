package bbangduck.bd.bbangduck.domain.model.emumerate;

import bbangduck.bd.bbangduck.global.common.EnumType;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public enum Difficulty implements EnumType {
    VERY_EASY("매우 쉬움"),
    EASY("쉬움"),
    NORMAL("보통"),
    DIFFICULT("어려움"),
    VERY_DIFFICULT("매우 어려움");

    private final String description;

    @Override
    public String getDescription() {
        return description;
    }
}
