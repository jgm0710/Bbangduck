package bbangduck.bd.bbangduck.domain.member.dto;

import bbangduck.bd.bbangduck.domain.auth.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import lombok.*;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 가입 요청 시 응답 Body 를 담을 Dto <br/>
 * 회원 정보 및 인증 토큰을 담고 있다.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignUpResponseDto {

    private MyProfileResponseDto memberInfo;

    private TokenDto tokenDto;

    public static MemberSignUpResponseDto convert(Member savedMember, TokenDto tokenDto) {
        return MemberSignUpResponseDto.builder()
                .memberInfo(MyProfileResponseDto.convert(savedMember))
                .tokenDto(tokenDto)
                .build();
    }
}
