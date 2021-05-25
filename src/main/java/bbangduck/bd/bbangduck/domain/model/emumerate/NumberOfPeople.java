package bbangduck.bd.bbangduck.domain.model.emumerate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum NumberOfPeople {
    ONE("한 사람"),
    TWO("두 사람"),
    THREE("세 사람"),
    FOUR("네 사람"),
    FIVE("다섯 사람");

    private final String description;
}
