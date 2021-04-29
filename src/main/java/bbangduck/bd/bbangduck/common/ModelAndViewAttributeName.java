package bbangduck.bd.bbangduck.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ModelAndViewAttributeName implements EnumType{
    STATUS("상태값 지정"),
    MESSAGE("메세지값 지정"),
    DATA("데이터값 지정")
    ;

    private final String description;

    @Override
    public String getDescription() {
        return null;
    }
}
