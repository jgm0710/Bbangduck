package bbangduck.bd.bbangduck.domain.member.service;

import bbangduck.bd.bbangduck.domain.file.service.FileStorageService;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.exception.FoundMemberIsWithdrawalOrBanException;
import bbangduck.bd.bbangduck.domain.member.repository.MemberPlayInclinationQueryRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberProfileImageRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberQueryRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.global.config.properties.MemberProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;

/**
 * @author Gumin Jeong
 * @since 2021-07-15
 */
@DisplayName("MemberService 단위 테스트")
class MemberServiceUnitTest {

    MemberRepository memberRepository = Mockito.mock(MemberRepository.class);
    MemberQueryRepository memberQueryRepository = Mockito.mock(MemberQueryRepository.class);
    MemberProfileImageRepository memberProfileImageRepository = Mockito.mock(MemberProfileImageRepository.class);
    FileStorageService fileStorageService = Mockito.mock(FileStorageService.class);
    MemberPlayInclinationQueryRepository memberPlayInclinationQueryRepository = Mockito.mock(MemberPlayInclinationQueryRepository.class);
    MemberProperties memberProperties = Mockito.mock(MemberProperties.class);

    MemberService memberService = new MemberService(
            memberRepository,
            memberQueryRepository,
            memberProfileImageRepository,
            fileStorageService,
            memberPlayInclinationQueryRepository,
            memberProperties
    );

    @Test
    @DisplayName("회원 ID 목록으로 회원 조회 - 회원 목록 중 한 회원이 탈퇴한 회원일 경우")
    public void getMembers() {
        //given
        List<Member> members = new ArrayList<>();
        for (long i = 0; i < 3; i++) {
            Member member = Member.builder()
                    .id(i)
                    .roles(Set.of(MemberRole.USER))
                    .build();
            members.add(member);
        }

        Member withdrawalMember = Member.builder()
                .id(10L)
                .roles(Set.of(MemberRole.WITHDRAWAL))
                .build();
        members.add(withdrawalMember);

        List<Long> memberIds = members.stream().map(Member::getId).collect(Collectors.toList());
        given(memberQueryRepository.findByMemberIds(memberIds)).willReturn(members);

        //when

        //then
        Assertions.assertThrows(FoundMemberIsWithdrawalOrBanException.class, () -> memberService.getMembers(memberIds));

    }

}