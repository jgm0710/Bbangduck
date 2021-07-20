package bbangduck.bd.bbangduck.domain.genre;

import lombok.RequiredArgsConstructor;

/**
 * 장르를 표시할 Enum
 *
 * @author Gumin Jeong
 * @since 2021-07-21
 */
@RequiredArgsConstructor
public enum GenreType {
    HORROR("공포"),
    THRILLER("스릴러"),
    ROMANCE("로멘스"),
    REASONING("추리"),
    EMOTIONAL("감성"),
    ADVENTURE("모험"),
    STEALTH("잠입"),
    CRIME("범죄"),
    COMEDY("코미디"),
    FANTASY("판타지"),
    ADULT("19금"),
    HISTORY("역사"),
    SF("SF"),
    MUSIC("음악"),
    DRAMA("드라마"),
    ACTION("액션"),
    MYSTERY("미스터리"),
    ARCADE("아케이드"),
    OUTDOOR("야외"),
    OTHER("기타"),
    ;

    private final String description;

}
