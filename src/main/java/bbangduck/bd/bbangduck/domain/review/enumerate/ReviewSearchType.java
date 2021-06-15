package bbangduck.bd.bbangduck.domain.review.enumerate;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: 2021-06-15 주석
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
