package bbangduck.bd.bbangduck.domain.theme.dto.service;

import bbangduck.bd.bbangduck.domain.genre.Genre;
import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.NumberOfPeople;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeRatingFilteringType;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeSortCondition;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeType;
import bbangduck.bd.bbangduck.global.common.util.PagingQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 테마 목록 조회 시 필요한 값들을 이동하기 위해 구현한 Service Dto
 *
 * @author jgm
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemeGetListDto implements PagingQuery {

    private Long pageNum;

    private Integer amount;

    private Genre genre;

    private ThemeType themeType;

    private ThemeRatingFilteringType rating;

    private NumberOfPeople numberOfPeople;

    private Difficulty difficulty;

    private Activity activity;

    private HorrorGrade horrorGrade;

    private ThemeSortCondition sortCondition;

    @Override
    public long getPageNum() {
        return pageNum;
    }

    @Override
    public int getAmount() {
        return amount;
    }
}
