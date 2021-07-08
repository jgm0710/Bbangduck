package bbangduck.bd.bbangduck.domain.member.service;

import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberPlayInclination;
import bbangduck.bd.bbangduck.domain.member.repository.MemberPlayInclinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 회원의 플레이 성향에 대한 비즈니스 로직을 구현한 Service
 *
 * @author jgm
 */
@Service
@RequiredArgsConstructor
public class MemberPlayInclinationService {

    private final MemberPlayInclinationRepository memberPlayInclinationRepository;

    @Transactional
    public void reflectingPropensityOfMemberToPlay(Member member, List<Genre> themeGenres) {
        themeGenres.forEach(genre -> {
            Optional<MemberPlayInclination> optionalMemberPlayInclination = memberPlayInclinationRepository.findByMemberAndGenre(member, genre);
            if (optionalMemberPlayInclination.isPresent()) {
                MemberPlayInclination memberPlayInclination = optionalMemberPlayInclination.get();
                memberPlayInclination.increasePlayCount();
            } else {
                MemberPlayInclination newPlayInclination = MemberPlayInclination.builder()
                        .member(member)
                        .genre(genre)
                        .playCount(1)
                        .build();

                memberPlayInclinationRepository.save(newPlayInclination);
            }
        });
    }
}
