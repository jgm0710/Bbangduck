package bbangduck.bd.bbangduck.domain.theme.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 테마를 플레이한 회원 목록 조회 시 정렬 조건
 *
 * @author Gumin Jeong
 * @since 2021-07-19
 */
@RequiredArgsConstructor
@Getter
public enum ThemeGetMemberListSortCondition {
    LATEST("최신 순"),
    OLDEST("오래된 순"),
    REVIEW_LIKE_COUNT_ASC("리뷰에 좋아요 적게 받은 순"),
    REVIEW_LIKE_COUNT_DESC("리뷰에 좋아요 많이 받은 순");

    private final String description;

    public static List<String> getNameList() {
        return Stream.of(ThemeGetMemberListSortCondition.values()).map(Enum::name).collect(Collectors.toList());
    }
}
