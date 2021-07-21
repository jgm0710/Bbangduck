package bbangduck.bd.bbangduck.domain.member.service;


import bbangduck.bd.bbangduck.domain.genre.Genre;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberPlayInclination;
import bbangduck.bd.bbangduck.domain.member.repository.MemberPlayInclinationRepository;
import bbangduck.bd.bbangduck.global.common.NullCheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void reflectingPropensityOfMemberToPlay(Member member, Genre themeGenre) {
        if (NullCheckUtils.isNull(themeGenre)) {
            return;
        }
        MemberPlayInclination memberPlayInclination = memberPlayInclinationRepository.findByMemberAndGenre(member, themeGenre)
                .orElse(MemberPlayInclination.init(member, themeGenre));
        memberPlayInclination.increasePlayCount();
        memberPlayInclinationRepository.save(memberPlayInclination);
    }
}
