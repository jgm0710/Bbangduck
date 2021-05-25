package bbangduck.bd.bbangduck.domain.model.emumerate;

import lombok.RequiredArgsConstructor;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 등에서 만족도를 나타내기 위한 Enum
 */
@RequiredArgsConstructor
public enum Satisfaction {
    VERY_BAD("별로에요 or 아주 나쁜"),
    BAD("아쉬워요 or 나쁜"),
    NORMAL("보통"),
    GOOD("좋아요"),
    VERY_GOOD("매우 좋아요");

    private final String description;
}
