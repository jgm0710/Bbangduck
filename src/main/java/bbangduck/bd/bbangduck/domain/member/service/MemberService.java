package bbangduck.bd.bbangduck.domain.member.service;

import bbangduck.bd.bbangduck.domain.file.service.FileStorageService;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberProfileImage;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNicknameDuplicateException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.member.service.dto.MemberProfileImageDto;
import bbangduck.bd.bbangduck.domain.member.service.dto.MemberUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 작성자 : 정구민 <br><br>
 * <p>
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

    private void checkDuplicateNickname(String nickname) {
        if (memberRepository.findByNickname(nickname).isPresent()) {
            throw new MemberNicknameDuplicateException(nickname);
        }
    }

    // TODO: 21. 5. 17. 프로필 이미지 업데이트 테스트

    /**
     * 없었을경우 잘 생성되는지
     * 있었을 경우 잘 변경되는지
     * 변경될 경우 기존 파일은 잘 삭제되는지
     * 회원을 찾을 수 없는 경우
     */
    @Transactional
    public void updateProfileImage(Long memberId, MemberProfileImageDto memberProfileImageDto) {
        Member findMember = getMember(memberId);
        MemberProfileImage memberProfileImage = findMember.getProfileImage();
        if (memberProfileImage == null) {
            findMember.createProfileImage(memberProfileImageDto);
        } else {
            Long oldFileStorageId = memberProfileImage.getFileStorageId();
            Long newFileStorageId = memberProfileImageDto.getFileStorageId();

            if (!oldFileStorageId.equals(newFileStorageId)) {
                fileStorageService.deleteFile(oldFileStorageId);
                findMember.updateProfileImage(memberProfileImageDto);
            }
        }
    }
}
