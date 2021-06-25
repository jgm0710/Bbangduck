package bbangduck.bd.bbangduck.domain.member.service;

import bbangduck.bd.bbangduck.domain.file.service.FileStorageService;
import bbangduck.bd.bbangduck.domain.member.dto.service.MemberProfileImageDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberPlayInclination;
import bbangduck.bd.bbangduck.domain.member.entity.MemberProfileImage;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRoomEscapeRecodesOpenStatus;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberSearchKeywordType;
import bbangduck.bd.bbangduck.domain.member.exception.FindMemberIsWithdrawalOrBanException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNicknameDuplicateException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberProfileImageNotFoundException;
import bbangduck.bd.bbangduck.domain.member.repository.MemberPlayInclinationQueryRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberProfileImageRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberQueryRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.global.config.properties.MemberProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

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

    private final MemberQueryRepository memberQueryRepository;

    private final MemberProfileImageRepository memberProfileImageRepository;

    private final FileStorageService fileStorageService;

    private final MemberPlayInclinationQueryRepository memberPlayInclinationQueryRepository;

    private final MemberProperties memberProperties;

    public Member getMember(Long memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        checkMemberIsWithdrawalOrBan(findMember.getRoles());
        return findMember;
    }

    private void checkMemberIsWithdrawalOrBan(Set<MemberRole> roles) {
        if (roles.contains(MemberRole.WITHDRAWAL) || roles.contains(MemberRole.BAN)) {
            throw new FindMemberIsWithdrawalOrBanException();
        }
    }

    /**
     * 기능 테스트 o
     * - 해당 회원의 장르별 횟수가 잘 나타나는지 확인 o
     * - 다른 회원의 장르별 횟수는 안나오는지 확인 o
     * - 상위 n 개만 잘 조회되는지 확인 o
     * - member, memberPlayInclination, genre 가 join 돼서 조회되는지 확인 o
     */
    public List<MemberPlayInclination> getMemberPlayInclinationTopN(Long memberId) {
        return memberPlayInclinationQueryRepository.findTopByMember(memberId, memberProperties.getPlayInclinationTopLimit());

    }

    // TODO: 2021-06-16 회원 전체 성향 목록 조회 기능 구현
    public List<MemberPlayInclination> getMemberPlayInclinations(Long memberId) {
        return memberPlayInclinationQueryRepository.findAllByMember(memberId);
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
    public void updateRoomEscapeRecodesOpenStatus(Long memberId, MemberRoomEscapeRecodesOpenStatus memberRoomEscapeRecodesOpenStatus) {
        Member findMember = getMember(memberId);
        findMember.updateRoomEscapeRecodesOpenStatus(memberRoomEscapeRecodesOpenStatus);
    }

    /**
     * 테스트 o
     *
     * 기능 테스트
     * - 이메일을 통한 회원 조회 성공 ?
     * - 닉네임을 통한 회원 조회 성공 ?
     *
     * 실패 테스트
     * - 닉네임, 이메일을 통한 회원 조회 실패 o
     */
    public Member searchMember(MemberSearchKeywordType searchType, String keyword) {
        return memberQueryRepository.findBySearchTypeAndKeyword(searchType, keyword).orElseThrow(() -> new MemberNotFoundException(searchType, keyword));
    }
}
