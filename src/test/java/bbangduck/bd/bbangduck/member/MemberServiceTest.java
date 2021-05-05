package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class MemberServiceTest extends BaseJGMServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("회원 조회 테스트")
    public void getMemberTest() {
        //given
        Member member = Member.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .build();

        Member save = memberRepository.save(member);

        entityManager.flush();
        entityManager.clear();

        //when
        Member findMember = memberService.getMember(save.getId());
        System.out.println("findMember = " + findMember.toString());

        //then
        assertEquals(findMember.getId(), member.getId());
        assertEquals(findMember.getEmail(), member.getEmail());
        assertEquals(findMember.getNickname(), member.getNickname());
    }

    @Test
    @DisplayName("회원 조회 시 회원을 찾을 수 없는 경우")
    public void getMember_NotFound() {
        //given

        //when

        //then
        assertThrows(MemberNotFoundException.class, () -> memberService.getMember(10000L));
    }

}