package bbangduck.bd.bbangduck.domain.member.dto;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignUpResponseDto {

    private MemberDetailDto memberInfo;

    private TokenDto tokenDto;


    public static MemberSignUpResponseDto init(Member savedMember, TokenDto tokenDto) {
        return MemberSignUpResponseDto.builder()
                .memberInfo(MemberDetailDto.memberToDetail(savedMember))
                .tokenDto(tokenDto)
                .build();
    }
}