package bbangduck.bd.bbangduck.domain.member.service;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.genre.Genre;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberPlayInclination;
import bbangduck.bd.bbangduck.domain.member.repository.MemberPlayInclinationRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("회원 플레이 성향 Service 통합 테스트")
class MemberPlayInclinationServiceIntegrationTest extends BaseTest {

    @Autowired
    MemberPlayInclinationService memberPlayInclinationService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberPlayInclinationRepository memberPlayInclinationRepository;

    @AfterEach
    void tearDown() {
        memberPlayInclinationRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 플레이 성향 반영 - 기존에 해당 장르를 플레이하지 않은 경우")
    public void reflectingPropensityOfMemberToPlay_NotPlayGenre() {
        //given
        Member member = Member.builder()
                .email("rpoMember@email.com")
                .nickname("rpoMember")
                .build();
        Member savedMember = memberRepository.save(member);

        Genre genre1 = Genre.ACTION;
        Genre genre2 = Genre.ADVENTURE;

        //when
        memberPlayInclinationService.reflectingPropensityOfMemberToPlay(member, genre1);
        memberPlayInclinationService.reflectingPropensityOfMemberToPlay(member, genre2);

        //then
        MemberPlayInclination memberPlayInclination1 = memberPlayInclinationRepository.findByMemberAndGenre(savedMember, genre1).orElseThrow(EntityNotFoundException::new);
        MemberPlayInclination memberPlayInclination2 = memberPlayInclinationRepository.findByMemberAndGenre(savedMember, genre2).orElseThrow(EntityNotFoundException::new);

        assertEquals(1, memberPlayInclination1.getPlayCount());
        assertEquals(1, memberPlayInclination2.getPlayCount());

        //final
    }

    @Test
    @DisplayName("회원 플레이 성향 반영 - 기본에 해당 장르를 플레이한 경우")
    public void reflectingPropensityOfMemberToPlay_AlreadyPlayedGenre() {
        //given
        Member member = Member.builder()
                .email("rpoMember@email.com")
                .nickname("rpoMember")
                .build();
        Member savedMember = memberRepository.save(member);

        Genre genre1 = Genre.ACTION;
        Genre genre2 = Genre.CRIME;


        //when
        memberPlayInclinationService.reflectingPropensityOfMemberToPlay(member, genre1);
        memberPlayInclinationService.reflectingPropensityOfMemberToPlay(member, genre2);

        memberPlayInclinationService.reflectingPropensityOfMemberToPlay(member, genre1);
        memberPlayInclinationService.reflectingPropensityOfMemberToPlay(member, genre2);

        //then
        MemberPlayInclination memberPlayInclination1 = memberPlayInclinationRepository.findByMemberAndGenre(savedMember, genre1).orElseThrow(EntityNotFoundException::new);
        MemberPlayInclination memberPlayInclination2 = memberPlayInclinationRepository.findByMemberAndGenre(savedMember, genre2).orElseThrow(EntityNotFoundException::new);

        assertEquals(2, memberPlayInclination1.getPlayCount());
        assertEquals(2, memberPlayInclination2.getPlayCount());

    }

}