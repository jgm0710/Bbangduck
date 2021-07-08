package bbangduck.bd.bbangduck.domain.member.service;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.genre.repository.GenreRepository;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberPlayInclination;
import bbangduck.bd.bbangduck.domain.member.repository.MemberPlayInclinationRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("회원 플레이 성향 Service 통합 테스트")
class MemberPlayInclinationServiceIntegrationTest extends BaseTest {

    @Autowired
    MemberPlayInclinationService memberPlayInclinationService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    GenreRepository genreRepository;

    @Autowired
    MemberPlayInclinationRepository memberPlayInclinationRepository;

    @Test
    @DisplayName("회원 플레이 성향 반영 - 기존에 해당 장르를 플레이하지 않은 경우")
    public void reflectingPropensityOfMemberToPlay_NotPlayGenre() {
        //given
        Member member = Member.builder()
                .email("rpoMember@email.com")
                .nickname("rpoMember")
                .build();
        Member savedMember = memberRepository.save(member);

        Genre genre1 = Genre.builder()
                .code("RPOFGR1")
                .name("genre1")
                .build();

        Genre genre2 = Genre.builder()
                .code("RPOFGR2")
                .name("genre2")
                .build();

        Genre savedGenre1 = genreRepository.save(genre1);
        Genre savedGenre2 = genreRepository.save(genre2);

        List<Genre> genres = new ArrayList<>();
        genres.add(savedGenre1);
        genres.add(savedGenre2);

        //when
        memberPlayInclinationService.reflectingPropensityOfMemberToPlay(member, genres);

        //then
        MemberPlayInclination memberPlayInclination1 = memberPlayInclinationRepository.findByMemberAndGenre(savedMember, savedGenre1).orElseThrow(EntityNotFoundException::new);
        MemberPlayInclination memberPlayInclination2 = memberPlayInclinationRepository.findByMemberAndGenre(savedMember, savedGenre2).orElseThrow(EntityNotFoundException::new);

        assertEquals(1, memberPlayInclination1.getPlayCount());
        assertEquals(1, memberPlayInclination2.getPlayCount());

        //final
        memberPlayInclinationRepository.delete(memberPlayInclination1);
        memberPlayInclinationRepository.delete(memberPlayInclination2);
        genreRepository.delete(savedGenre1);
        genreRepository.delete(savedGenre2);
        memberRepository.delete(savedMember);

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

        Genre genre1 = Genre.builder()
                .code("RPOFGR1")
                .name("genre1")
                .build();

        Genre genre2 = Genre.builder()
                .code("RPOFGR2")
                .name("genre2")
                .build();

        Genre savedGenre1 = genreRepository.save(genre1);
        Genre savedGenre2 = genreRepository.save(genre2);

        List<Genre> genres = new ArrayList<>();
        genres.add(savedGenre1);
        genres.add(savedGenre2);


        //when
        memberPlayInclinationService.reflectingPropensityOfMemberToPlay(member, genres);
        memberPlayInclinationService.reflectingPropensityOfMemberToPlay(member, genres);

        //then
        MemberPlayInclination memberPlayInclination1 = memberPlayInclinationRepository.findByMemberAndGenre(savedMember, savedGenre1).orElseThrow(EntityNotFoundException::new);
        MemberPlayInclination memberPlayInclination2 = memberPlayInclinationRepository.findByMemberAndGenre(savedMember, savedGenre2).orElseThrow(EntityNotFoundException::new);

        assertEquals(2, memberPlayInclination1.getPlayCount());
        assertEquals(2, memberPlayInclination2.getPlayCount());

        //final
        memberPlayInclinationRepository.delete(memberPlayInclination1);
        memberPlayInclinationRepository.delete(memberPlayInclination2);
        genreRepository.delete(savedGenre1);
        genreRepository.delete(savedGenre2);
        memberRepository.delete(savedMember);

    }

}