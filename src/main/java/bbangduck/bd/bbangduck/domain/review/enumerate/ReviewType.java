package bbangduck.bd.bbangduck.domain.review.enumerate;

import bbangduck.bd.bbangduck.global.common.EnumType;
import lombok.RequiredArgsConstructor;

/**
 * 작성자 : 정구민 <br><br>
 *
 * Review 작성 시
 * 간단 기록, 상세 리뷰, 추가 설문 리뷰로 분류됨
 * 이를 구분하기 위해 사용될 Enum type
 */
@RequiredArgsConstructor
public enum ReviewType implements EnumType {
    BASE("해당 테마 플레이 횟수, 성공 여부, 탈출 기록 등에 대한 간단한 리뷰 작성"),
    DETAIL("사진, 감상평 등의 추가 정보를 기입하는 리뷰 작성"),
    ;

    private final String description;

    @Override
    public String getDescription() {
        return description;
    }
}
