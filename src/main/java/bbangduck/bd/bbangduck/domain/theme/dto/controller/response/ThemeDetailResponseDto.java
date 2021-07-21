package bbangduck.bd.bbangduck.domain.theme.dto.controller.response;

import bbangduck.bd.bbangduck.domain.genre.Genre;
import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.NumberOfPeople;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

/**
 * 테마 상세 조회에 대한 응답 Body Data 를 담을 Dto
 *
 * @author jgm
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemeDetailResponseDto {

    private Long themeId;

    private ThemeImageResponseDto themeImage;

    private String themeName;

    private String  themeDescription;

    private Float themeRating;

    private Genre themeGenre;

    private ThemeShopSimpleInfoResponseDto shopInfo;

    private LocalTime playTime;

    private List<NumberOfPeople> numberOfPeoples;

    private Difficulty difficulty;

    private Activity activity;

    private HorrorGrade horrorGrade;

    public static ThemeDetailResponseDto convert(Theme theme) {
        return ThemeDetailResponseDto.builder()
                .themeId(theme.getId())
                .themeImage(ThemeImageResponseDto.convert(theme.getThemeImage()))
                .themeName(theme.getName())
                .themeDescription(theme.getDescription())
                .themeRating(theme.getRating())
                .themeGenre(theme.getGenre())
                .shopInfo(ThemeShopSimpleInfoResponseDto.convert(theme.getShop()))
                .playTime(theme.getPlayTime())
                .numberOfPeoples(theme.getNumberOfPeoples())
                .difficulty(theme.getDifficulty())
                .activity(theme.getActivity())
                .horrorGrade(theme.getHorrorGrade())
                .build();
    }
}
