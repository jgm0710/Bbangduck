package bbangduck.bd.bbangduck.domain.member.dto.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * 작성자 : Gumin Jeong
 *
 * 작성 일자 :  2021-06-17
 *
 * 회원의 플레이 성향을 조회 API 의 응답 Body Data 를 담을 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetMemberPlayInclinationsResponseDto {

    List<MemberPlayInclinationResponseDto> playInclinations;

    private long totalThemeEvaluatesCount;

}
