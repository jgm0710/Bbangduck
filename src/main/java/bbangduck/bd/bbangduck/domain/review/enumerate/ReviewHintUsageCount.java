package bbangduck.bd.bbangduck.domain.review.enumerate;

import bbangduck.bd.bbangduck.global.common.EnumType;
import lombok.RequiredArgsConstructor;

/**
 * 작성자 : Gumin Jeong
 * <p>
 * 작성 일자 : 2021-06-16
 * <p>
 * 리뷰에 등록할 힌트 사용 개수를 표현할 Enum
 */
@RequiredArgsConstructor
public enum ReviewHintUsageCount implements EnumType {
    NONE("힌트를 사용하지 않음"),
    ONE("힌트 1개 사용"),
    TWO("힌트 2개 사용"),
    THREE_OR_MORE("힌트 3개 이상 사용"),
    ;

    public final String description;

    @Override
    public String getDescription() {
        return description;
    }
}
