package bbangduck.bd.bbangduck.domain.member.dto.controller.response;

import bbangduck.bd.bbangduck.domain.review.dto.entity.ReviewRecodesCountsDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 작성자 : Gumin Jeong
 * <p>
 * 작성 일자 : 2021-06-17
 * <p>
 * 회원 프로필 조회 시 회원의 방탈출 현황을 담기 위한 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberRoomEscapeStatusResponseDto {

    private Integer challengesCount;

    private Integer successCount;

    private Integer failCount;

    public static MemberRoomEscapeStatusResponseDto convert(ReviewRecodesCountsDto reviewRecodesCountsDto) {
        return MemberRoomEscapeStatusResponseDto.builder()
                .challengesCount(reviewRecodesCountsDto.getTotalRecodesCount())
                .successCount(reviewRecodesCountsDto.getSuccessRecodesCount())
                .failCount(reviewRecodesCountsDto.getFailRecodesCount())
                .build();
    }

}
