package bbangduck.bd.bbangduck.domain.member.dto.controller.response;

import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 작성자 : Gumin Jeong
 * <p>
 * 작성 일자 : 2021-06-17
 * <p>
 * 회원 프로필 조회 응답 시 회원의 플레이 성향에 응답될 장르에 대한 간단한 정보를 담을 응답 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberPlayInclinationGenreSimpleInfoResponseDto {

    private String genreCode;

    private String genreName;

    public static MemberPlayInclinationGenreSimpleInfoResponseDto convert(Genre genre) {
        return MemberPlayInclinationGenreSimpleInfoResponseDto.builder()
                .genreCode(genre.getCode())
                .genreName(genre.getName())
                .build();
    }
}
