package bbangduck.bd.bbangduck.domain.member.dto.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 작성자 : Gumin Jeong
 * <p>
 * 작성 일자 : 2021-06-19
 * <p>
 * 닉네임 사용 가능 여부 체크 요청 Body Data 를 담을 Dto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberCheckIfNicknameIsAvailableRequestDto {

    @NotBlank(message = "중복 체크할 닉네임을 기입해 주세요.")
    private String nickname;

}
