package bbangduck.bd.bbangduck.domain.theme.dto.controller.request;

import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.NumberOfPeople;
import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemeGetListDto;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeRatingFilteringType;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeSortCondition;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 테마 목록 조회 요청 시 필요한 요청 Query Parameters 를 담을 Dto
 *
 * @author jgm
 */
@Data
@Builder
@AllArgsConstructor
public class ThemeGetListRequestDto {

    private String genreCode;

    private ThemeType themeType;

    private ThemeRatingFilteringType rating;

    private NumberOfPeople numberOfPeople;

    private Difficulty difficulty;

    private Activity activity;

    private HorrorGrade horrorGrade;

    private ThemeSortCondition sortCondition;

    public ThemeGetListRequestDto() {
        this.genreCode = null;
        this.themeType = null;
        this.rating = null;
        this.difficulty = null;
        this.activity = null;
        this.horrorGrade = null;
    }

    public ThemeGetListDto toServiceDto() {
        return ThemeGetListDto.builder()
                .genreCode(genreCode)
                .themeType(themeType)
                .rating(rating)
                .difficulty(difficulty)
                .activity(activity)
                .horrorGrade(horrorGrade)
                .sortCondition(sortCondition)
                .build();
    }

}
