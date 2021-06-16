package bbangduck.bd.bbangduck.domain.review.enumerate;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 작성자 : Gumin Jeong
 *
 * 작성 일자 : 2021-06-15
 *
 * 리뷰 목록 조회 시 검색 조건을 지정하기 위해 사용되는 Enum
 */
@RequiredArgsConstructor
public enum ReviewSearchType {
    TOTAL("전체 리뷰를 대상으로 검색"),
    SUCCESS("클리어 성공한 리뷰를 대상으로 검색"),
    FAIL("클리어 실패한 리뷰를 대상으로 검색");

    private final String description;

    public static List<String> getNameList() {
        return Stream.of(ReviewSearchType.values()).map(Enum::name).collect(Collectors.toList());
    }
}
