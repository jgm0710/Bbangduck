package bbangduck.bd.bbangduck.domain.member.dto;

import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignUpDto {

    @Email(message = "Email 형식에 맞게 Email 을 기입해 주세요.")
    @NotBlank(message = "Email 을 입력해 주세요.")
    private String email;

    @NotBlank(message = "Nickname 을 입력해 주세요.")
    private String nickname;

    private String password;

    private SocialType socialType;

    private String socialId;

//.email()
//.nickname()
//.password()
//.socialType()
//.socialId()
}
