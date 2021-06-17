package bbangduck.bd.bbangduck.domain.member.dto.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 자기 소개 수정 요청 시 요청 Body 의 Data 를 담을 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateDescriptionRequestDto {

    @NotNull(message = "변경할 자기소개를 기입해 주세요.")
    private String description;

}
