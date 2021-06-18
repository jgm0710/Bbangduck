package bbangduck.bd.bbangduck.domain.member.dto.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 작성자 : Gumin Jeong
 * <p>
 * 작성 일자 : 2021-06-19
 * <p>
 * 이메일 사용 가능 여부 체크 요청 Body Data 를 담을 Dto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckIfEmailIsAvailableRequestDto {

    @NotBlank(message = "중복 확인할 이메일을 기입해 주세요.")
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;

}
