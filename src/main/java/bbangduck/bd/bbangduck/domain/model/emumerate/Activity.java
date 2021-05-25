package bbangduck.bd.bbangduck.domain.model.emumerate;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Activity {
    LITTLE_ACTIVITY("조금 활동"),
    NORMAL("보통"),
    VERY_ACTIVITY("매우 활동");

    private final String description;

}
