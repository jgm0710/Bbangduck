package bbangduck.bd.bbangduck.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ModelAndViewAttributeName implements EnumType{
    STATUS("status","상태값 지정"),
    MESSAGE("message","메세지값 지정"),
    DATA("data","데이터값 지정")
    ;

    private final String attributeName;
    private final String description;


    @Override
    public String getDescription() {
        return null;
    }
}
