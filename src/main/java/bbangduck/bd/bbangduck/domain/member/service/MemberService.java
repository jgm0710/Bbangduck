package bbangduck.bd.bbangduck.domain.member.service;

import bbangduck.bd.bbangduck.domain.file.service.FileStorageService;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberProfileImage;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNicknameDuplicateException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberProfileImageNotFoundException;
import bbangduck.bd.bbangduck.domain.member.repository.MemberProfileImageRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.member.service.dto.MemberProfileImageDto;
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

    private final MemberProfileImageRepository memberProfileImageRepository;

    private final FileStorageService fileStorageService;

    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }

    private void checkDuplicateNickname(String nickname) {
        if (memberRepository.findByNickname(nickname).isPresent()) {
            throw new MemberNicknameDuplicateException(nickname);
        }
    }

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

    @Transactional
    public void deleteProfileImage(Long memberId) {
        Member findMember = getMember(memberId);
        MemberProfileImage profileImage = findMember.getProfileImage();
        if (profileImage == null) {
            throw new MemberProfileImageNotFoundException();
        } else {
            findMember.deleteProfileImage(memberProfileImageRepository);
            fileStorageService.deleteFile(profileImage.getFileStorageId());
        }
    }

    @Transactional
    public void updateNickname(Long memberId, String nickname) {
        Member findMember = getMember(memberId);
        if (!findMember.getNickname().equals(nickname)) {
            checkDuplicateNickname(nickname);
        }
        findMember.updateNickname(nickname);
    }

    @Transactional
    public void updateDescription(Long memberId, String description) {
        Member findMember = getMember(memberId);
        findMember.updateDescription(description);
    }

    @Transactional
    public void toggleRoomEscapeRecodesOpenYN(Long memberId) {
        Member findMember = getMember(memberId);
        findMember.toggleRoomEscapeRecodesOpenYN();
    }
}
