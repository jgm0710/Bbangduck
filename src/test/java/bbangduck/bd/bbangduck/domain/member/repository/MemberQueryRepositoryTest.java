package bbangduck.bd.bbangduck.domain.member.repository;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberSearchKeywordType;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 회원에 대한 보다 복잡한 쿼리를 구현하기 위한 Repository
 *
 * @author jgm
 */
class MemberQueryRepositoryTest extends BaseTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberQueryRepository memberQueryRepository;

    @ParameterizedTest
    @MethodSource("parametersForFindBySearchTypeAndKeyword")
    @DisplayName("이메일, 닉네임을 통한 회원 조회")
    public void findBySearchTypeAndKeyword(MemberSearchKeywordType searchKeywordType, String keyword) {
        //given
        Member member1 = Member.builder()
                .email("fstmember1@email.com")
                .nickname("fstmember1")
                .build();

        Member member2 = Member.builder()
                .email("fstmember2@email.com")
                .nickname("fstmember2")
                .build();

        Member save1 = memberRepository.save(member1);
        Member save2 = memberRepository.save(member2);

        //when
        Member findMember = memberQueryRepository.findBySearchTypeAndKeyword(searchKeywordType, keyword).orElseThrow(MemberNotFoundException::new);

        //then
        assertEquals(save1.getId(), findMember.getId());
        assertEquals(save1.getEmail(), findMember.getEmail());
        assertEquals(save1.getNickname(), findMember.getNickname());

        memberRepository.delete(save1);
        memberRepository.delete(save2);

    }

    private static Stream<Arguments> parametersForFindBySearchTypeAndKeyword() {
        return Stream.of(
                Arguments.of(MemberSearchKeywordType.EMAIL, "fstmember1@email.com"),
                Arguments.of(MemberSearchKeywordType.NICKNAME, "fstmember1")
        );
    }
}