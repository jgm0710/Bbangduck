package bbangduck.bd.bbangduck.domain.member.service;

import bbangduck.bd.bbangduck.domain.file.repository.FileStorageRepository;
import bbangduck.bd.bbangduck.domain.file.service.FileStorageService;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.SocialAccount;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNicknameDuplicateException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.member.service.dto.MemberProfileImageDto;
import bbangduck.bd.bbangduck.domain.member.service.dto.MemberUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    private final FileStorageService fileStorageService;

    public Member getMember(Long memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        log.debug("findMember : {}", findMember);

        return findMember;
    }

    @Transactional
    public void updateMember(Long memberId, MemberUpdateDto updateDto) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        String updateNickname = updateDto.getNickname();
        if (!findMember.getNickname().equals(updateNickname)) {
            checkDuplicateNickname(updateNickname);
        }
        findMember.updateProfile(updateDto);
    }

    private void checkDuplicateNickname(String nickname) {
        if (memberRepository.findByNickname(nickname).isPresent()) {
            throw new MemberNicknameDuplicateException(nickname);
        }
    }

    // TODO: 21. 5. 17. 프로필 이미지 업데이트 테스트
    /**
     * 잘 변경 되는지
     * 기존 프로필 이미지는 사라지는지
     *
     */
    @Transactional
    public void updateProfileImage(Long memberId, MemberProfileImageDto memberProfileImageDto) {
        Member findMember = getMember(memberId);
        findMember.isChangeProfileImage(memberProfileImageDto);
        findMember.updateProfileImage(memberProfileImageDto);
    }
}
