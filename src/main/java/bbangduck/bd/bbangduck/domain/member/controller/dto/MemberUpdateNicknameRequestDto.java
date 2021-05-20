package bbangduck.bd.bbangduck.domain.member.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 닉네임 수정 요청 시 요청 Body 의 Data 를 담기 위한 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateNicknameRequestDto {

    @NotBlank(message = "변경할 Nickname 을 기입해 주세요.")
    private String nickname;

}
