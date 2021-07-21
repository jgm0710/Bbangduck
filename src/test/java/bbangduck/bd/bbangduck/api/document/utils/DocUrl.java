package bbangduck.bd.bbangduck.api.document.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DocUrl {
    MEMBER_ROLE("memberRole", "회원권한"),
    ACTIVITY("activity", "활동성"),
    DIFFICULTY("difficulty", "난이도"),
    NUMBER_OF_PEOPLE("numberOfPeople", "참여 인원"),
    HORROR_GRADE("horrorGrade", "공포도"),
    SATISFACTION("satisfaction", "만족도"),
    THEME_TYPE("themeType", "테마 유형"),
    THEME_RATING_FILTERING_TYPE("themeRatingFilteringType", "테마 목록 조회 평점 필터링 조건"),
    THEME_SORT_CONDITION("themeSortCondition", "테마 목록 조회 정렬 조건"),
    MEMBER_ROOM_ESCAPE_RECODES_OPEN_STATUS("memberRoomEscapeRecodesOpenStatus", "회원 방탈출 공개 상태"),
    MEMBER_SEARCH_KEYWORD_TYPE("memberSearchKeywordType", "회원 검색 키워드 타입"),
    SOCIAL_TYPE("socialType", "소셜 타입"),
    REVIEW_HINT_USAGE_COUNT("reviewHintUsageCount", "리뷰 힌트 사용 개수"),
    REVIEW_SEARCH_TYPE("reviewSearchType", "리뷰 목록 조회 조건"),
    REVIEW_SORT_CONDITION("reviewSortCondition", "리뷰 목록 조회 정렬 조건"),
    REVIEW_TYPE("reviewType", "리뷰 타입"),
    GENRE("genre","장르")
    ;

    @Getter
    private final String pageId;
    @Getter
    private final String text;
}