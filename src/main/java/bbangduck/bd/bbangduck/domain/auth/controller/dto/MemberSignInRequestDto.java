package bbangduck.bd.bbangduck.domain.auth.controller.dto;

import bbangduck.bd.bbangduck.domain.auth.service.dto.MemberSignInDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 일반 로그인 요청 시 필요한 Email, Password 를 담을 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignInRequestDto {

    @NotBlank(message = "로그인을 위한 이메일을 입력해 주세요.")
    private String email;

    @NotBlank(message = "로그인을 위한 비밀번호를 입력해 주세요.")
    private String password;

    public MemberSignInDto toServiceDto() {
        return MemberSignInDto.builder()
                .email(email)
                .password(password)
                .build();
    }

}
