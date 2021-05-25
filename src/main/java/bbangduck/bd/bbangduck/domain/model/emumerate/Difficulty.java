package bbangduck.bd.bbangduck.domain.model.emumerate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Difficulty {
    VERY_EASY("매우 쉬움"),
    EASY("쉬움"),
    NORMAL("보통"),
    DIFFICULT("어려움"),
    VERY_DIFFICULT("매우 어려움");

    private final String description;
}
