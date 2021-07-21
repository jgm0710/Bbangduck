package bbangduck.bd.bbangduck.domain.theme.dto.controller.response;


import bbangduck.bd.bbangduck.domain.genre.Genre;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemeAnalysis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 테마 분석에 대한 응답 Body Data 를 담을 Dto
 *
 * @author jgm
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemeAnalysesResponseDto {
    private Genre genre;
    private Long evaluatedCount;

    public static ThemeAnalysesResponseDto convert(ThemeAnalysis themeAnalysis) {
        return ThemeAnalysesResponseDto.builder()
                .genre(themeAnalysis.getGenre())
                .evaluatedCount(themeAnalysis.getEvaluatedCount())
                .build();
    }
}
