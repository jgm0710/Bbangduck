package bbangduck.bd.bbangduck.domain.review.controller.dto.response;

import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 조회 시 리뷰가 등록된 체감 테마 장르에 대한 값들을 응답하기 위해,
 * 장르에 대한 응답 Data 를 담을 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewPerceivedThemeGenreResponseDto {

    private Long genreId;

    private String genreCode;

    private String genreName;

    public static ReviewPerceivedThemeGenreResponseDto convert(Genre genre) {
        return ReviewPerceivedThemeGenreResponseDto.builder()
                .genreId(genre.getId())
                .genreCode(genre.getCode())
                .genreName(genre.getName())
                .build();
    }
}
