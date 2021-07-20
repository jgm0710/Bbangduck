package bbangduck.bd.bbangduck.domain.model.emumerate;

import bbangduck.bd.bbangduck.global.common.EnumType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Activity implements EnumType {
    LITTLE_ACTIVITY("조금 활동"),
    NORMAL("보통"),
    VERY_ACTIVITY("매우 활동");

    private final String description;

    @Override
    public String getDescription() {
        return description;
    }
}
