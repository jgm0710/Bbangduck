package bbangduck.bd.bbangduck.domain.member.service;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 관리를 위한 Service
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    public Member getMember(Long memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        log.debug("findMember : {}", findMember.toString());
        return findMember;
    }

    // TODO: 21. 5. 5. 회원 정보 수정 기능 구현
}
