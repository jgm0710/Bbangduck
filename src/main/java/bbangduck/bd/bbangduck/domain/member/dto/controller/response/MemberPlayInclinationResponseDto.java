package bbangduck.bd.bbangduck.domain.member.dto.controller.response;

import bbangduck.bd.bbangduck.domain.genre.Genre;
import bbangduck.bd.bbangduck.domain.member.entity.MemberPlayInclination;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 작성자 : Gumin Jeong
 * <p>
 * 작성 일자 : 2021-06-17
 * <p>
 * 회원 프로필 조회 등에서 회원의 플레이 성향에 대한 응답 Data 를 담을 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberPlayInclinationResponseDto {

    private Genre genre;

    private Long playCount;

    public static MemberPlayInclinationResponseDto convert(MemberPlayInclination memberPlayInclination) {
        return MemberPlayInclinationResponseDto.builder()
                .genre(memberPlayInclination.getGenre())
                .playCount(memberPlayInclination.getPlayCount())
                .build();
    }

}
