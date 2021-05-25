package bbangduck.bd.bbangduck.domain.model.emumerate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum HorrorGrade {
    LITTLE_HORROR("조금 공포"),
    NORMAL("보통"),
    VERY_HORROR("매우 공포");

    private final String description;
}
