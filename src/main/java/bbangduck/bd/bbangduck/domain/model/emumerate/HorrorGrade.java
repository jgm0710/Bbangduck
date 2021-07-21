package bbangduck.bd.bbangduck.domain.model.emumerate;

import bbangduck.bd.bbangduck.global.common.EnumType;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public enum HorrorGrade implements EnumType {
    LITTLE_HORROR("조금 공포"),
    NORMAL("보통"),
    VERY_HORROR("매우 공포");

    private final String description;

    @Override
    public String getDescription() {
        return description;
    }
}
