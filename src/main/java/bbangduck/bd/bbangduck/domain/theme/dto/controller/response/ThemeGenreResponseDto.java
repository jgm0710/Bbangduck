package bbangduck.bd.bbangduck.domain.theme.dto.controller.response;

import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 테마와 관련된 Genre 의 값을 응답 Data 에 싣기 위한 Dto
 *
 * @author jgm 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemeGenreResponseDto {

    private Long genreId;

    private String genreCode;

    private String genreName;

    public static ThemeGenreResponseDto convert(Genre genre) {
        return ThemeGenreResponseDto.builder()
                .genreId(genre.getId())
                .genreCode(genre.getCode())
                .genreName(genre.getName())
                .build();
    }
}
