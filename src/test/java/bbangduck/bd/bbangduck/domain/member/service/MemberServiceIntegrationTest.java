package bbangduck.bd.bbangduck.domain.member.service;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Gumin Jeong
 * @since 2021-07-15
 */
@DisplayName("MemberService 통합 테스트")
class MemberServiceIntegrationTest extends BaseTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 ID 목록으로 회원 조회")
    public void getMembers() {
        //given
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Member member = Member.builder()
                    .roles(Set.of(MemberRole.USER))
                    .build();
            memberRepository.save(member);
            members.add(member);
        }

        Member otherMember = Member.builder()
                .roles(Set.of(MemberRole.USER))
                .build();
        memberRepository.save(otherMember);


        List<Long> memberIds = members.stream().map(Member::getId).collect(Collectors.toList());

        //when
        List<Member> findMembers = memberService.getMembers(memberIds);

        //then
        assertEquals(3, findMembers.size(), "조회된 회원의 수는 3개이다.");
        boolean noneMatch = findMembers.stream().noneMatch(member -> member.getId().equals(otherMember.getId()));
        assertTrue(noneMatch, "조회된 회원 목록에는 조회 시 기입한 ID 인 회원은 존재하지 않아야 한다.");
    }

    @Test
    @DisplayName("회원 ID 목록으로 회원 조회 - 회원을 찾을 수 없는 경우")
    public void getMembers_MemberNotFound() {
        //given
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Member member = Member.builder()
                    .roles(Set.of(MemberRole.USER))
                    .build();
            memberRepository.save(member);
            members.add(member);
        }


        List<Long> memberIds = members.stream().map(Member::getId).collect(Collectors.toList());
        memberIds.add(1000L);

        //when

        //then
        assertThrows(MemberNotFoundException.class, () -> memberService.getMembers(memberIds));
    }

}