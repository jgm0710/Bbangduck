package bbangduck.bd.bbangduck.domain.review.entity.enumerate;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 작성자 : 정구민 <br><br>
 *
 * Review 작성 시
 * 간단 기록, 상세 리뷰, 추가 설문 리뷰로 분류됨
 * 이를 구분하기 위해 사용될 Enum type
 */
@RequiredArgsConstructor
public enum ReviewType {
    SIMPLE("해당 테마 플레이 횟수, 성공 여부, 탈출 기록 등에 대한 간단한 리뷰 작성"),
    DETAIL("사진, 감상평 등의 추가 정보를 기입하는 리뷰 작성"),
//    DEEP("테마에 대한 추가적인 설문까지 완료한 리뷰 작성"),
    ;

    private final String description;

    public static List<String> getNameList() {
        return Stream.of(ReviewType.values()).map(Enum::name).collect(Collectors.toList());
    }

}
