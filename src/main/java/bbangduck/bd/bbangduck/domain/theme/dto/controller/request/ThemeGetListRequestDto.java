package bbangduck.bd.bbangduck.domain.theme.dto.controller.request;

import bbangduck.bd.bbangduck.domain.genre.Genre;
import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.NumberOfPeople;
import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemeGetListDto;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeRatingFilteringType;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeSortCondition;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeType;
import bbangduck.bd.bbangduck.global.common.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

/**
 * 테마 목록 조회 요청 시 필요한 요청 Query Parameters 를 담을 Dto
 *
 * @author jgm
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemeGetListRequestDto implements PageRequest {

    @Min(value = 1, message = "페이지 번호는 1보다 작을 수 없습니다.")
    private Long pageNum = 1L;

    @Range(min = 1, max = 100, message = "조회 가능 수량은 1~100 사이 입니다.")
    private Integer amount = 20;

    private Genre genre = null;

    private ThemeType themeType = null;

    private ThemeRatingFilteringType rating = null;

    private NumberOfPeople numberOfPeople = null;

    private Difficulty difficulty = null;

    private Activity activity = null;

    private HorrorGrade horrorGrade = null;

    private ThemeSortCondition sortCondition = ThemeSortCondition.LATEST;

    public ThemeGetListDto toServiceDto() {
        return ThemeGetListDto.builder()
                .pageNum(pageNum)
                .amount(amount)
                .genre(genre)
                .themeType(themeType)
                .rating(rating)
                .difficulty(difficulty)
                .activity(activity)
                .horrorGrade(horrorGrade)
                .sortCondition(sortCondition)
                .build();
    }

    @Override
    public long getPageNum() {
        return pageNum;
    }

    @Override
    public int getAmount() {
        return amount;
    }
}
