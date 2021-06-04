package bbangduck.bd.bbangduck.domain.review.entity.enumerate;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 목록 조회 시 정렬 조건에 대한 Enum
 */
@RequiredArgsConstructor
public enum ReviewSortCondition {
    LATEST("최신 순"),
    OLDEST("오래된 순"),
    RATING_DESC("평점 높은 순 (같은 평점일 경우 최신 순)"),
    RATING_ASC("평점 낮은 순 (같은 평점일 경우 최신 순)"),
    LIKE_COUNT_DESC("좋아요 등록 수 많은 순 (같은 좋아요 개수일 경우 최신 순)"),
    LIKE_COUNT_ASC("좋아요 등록 수 적은 순 (같은 좋아요 개수일 경우 최신 순)");

    private final String description;

    public String getDescription() {
        return description;
    }

    public static List<String> getNameList() {
        return Stream.of(ReviewSortCondition.values()).map(Enum::name).collect(Collectors.toList());
    }
}
