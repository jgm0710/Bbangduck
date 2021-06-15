package bbangduck.bd.bbangduck.domain.auth.dto.controller;

import bbangduck.bd.bbangduck.domain.auth.dto.service.MemberSignUpDto;
import bbangduck.bd.bbangduck.domain.member.entity.enumerate.SocialType;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 가입 요청 시 요청 Body 의 데이터를 담기 위한 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSocialSignUpRequestDto {

    @Email(message = "Email 형식에 맞게 Email 을 기입해 주세요.")
    @NotBlank(message = "Email 을 입력해 주세요.")
    private String email;

    @NotBlank(message = "Nickname 을 입력해 주세요.")
    private String nickname;

    @NotNull(message = "Social Type 을 입력해 주세요.")
    private SocialType socialType;

    @NotBlank(message = "Social ID 를 입력해 주세요.")
    private String  socialId;

    public MemberSignUpDto toServiceDto() {
        return MemberSignUpDto.builder()
                .email(this.email)
                .nickname(this.nickname)
                .password(null)
                .socialType(this.socialType)
                .socialId(this.socialId)
                .build();
    }
}
