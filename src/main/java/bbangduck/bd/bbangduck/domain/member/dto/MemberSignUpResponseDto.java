package bbangduck.bd.bbangduck.domain.member.dto;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 가입 요청 시 응답 Body 를 담을 Dto <br/>
 * 회원 정보 및 인증 토큰을 담고 있다.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSignUpResponseDto {

    private MemberDetailDto memberInfo;

    private TokenDto tokenDto;

    @Builder
    public MemberSignUpResponseDto(MemberDetailDto memberInfo, TokenDto tokenDto) {
        this.memberInfo = memberInfo;
        this.tokenDto = tokenDto;
    }

    public static MemberSignUpResponseDto convert(Member savedMember, TokenDto tokenDto) {
        return MemberSignUpResponseDto.builder()
                .memberInfo(MemberDetailDto.convert(savedMember))
                .tokenDto(tokenDto)
                .build();
    }
}
