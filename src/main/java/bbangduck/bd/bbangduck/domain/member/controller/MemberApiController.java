package bbangduck.bd.bbangduck.domain.member.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.member.dto.controller.request.*;
import bbangduck.bd.bbangduck.domain.member.dto.controller.response.GetMemberPlayInclinationsResponseDto;
import bbangduck.bd.bbangduck.domain.member.dto.controller.response.MemberMyProfileResponseDto;
import bbangduck.bd.bbangduck.domain.member.dto.controller.response.MemberPlayInclinationResponseDto;
import bbangduck.bd.bbangduck.domain.member.dto.controller.response.MemberProfileResponseDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberPlayInclination;
import bbangduck.bd.bbangduck.domain.member.exception.UpdateDifferentMemberException;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.review.dto.entity.ReviewRecodesCountsDto;
import bbangduck.bd.bbangduck.domain.review.service.ReviewService;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static bbangduck.bd.bbangduck.global.common.ThrowUtils.hasErrorsThrow;

/**
 * 작성자 : 정구민 <br><br>
 * <p>
 * 회원 관리에 대한 요청을 다루기 위한 Controller
 * 회원 조회, 회원 수정 등
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Slf4j
public class MemberApiController {

    private final MemberService memberService;

    private final ReviewService reviewService;

    /**
     * 문서화 완료, 테스트 완료
     * 기능 테스트 o
     * - 다른 회원의 프로필을 조회할 경우 o
     * -- 회원 ID o
     * -- 프로필 이미지 o
     * -- 닉네임 o
     * -- 자기소개 o
     * -- 방탈출 현황 o
     * -- 방탈출 기록 공개 상태 o
     * -- 회원 성향 o
     *
     * - 자신의 프로필을 조회할 경우 o
     * -- 회원 ID o
     * -- 프로필 이미지 o
     * -- 닉네임 o
     * -- 자기소개 o
     * -- 방탈출 현황 o
     * -- 방탈출 기록 공개 상태 o
     * -- 회원 성향 o
     *
     * -- 이메일 o
     * -- 어떤 소셜 계정으로 가입된 회원인지 o
     * -- 생성 일자 o
     * -- 개인정보 수정 일자 o
     *
     *
     *
     * 실패 테스트
     * - 조회된 회원이 탈퇴하거나, 계정이 정지된 회원일 경우 조회 불가 o
     * - 인증되지 않은 회원일 경우 리소스 접근 불가 o
     * - 탈퇴한 회원일 경우 리소스 접근 불가 o
     */
    @GetMapping("/{memberId}/profiles")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<MemberProfileResponseDto> getProfile(
            @PathVariable Long memberId,
            @CurrentUser Member currentMember
    ) {
        log.debug("currentMember : {}", currentMember.toString());
        Member findMember = memberService.getMember(memberId);
        List<MemberPlayInclination> memberPlayInclinationTopN = memberService.getMemberPlayInclinationTopN(memberId);
        ReviewRecodesCountsDto reviewRecodesCounts = reviewService.getReviewRecodesCounts(memberId);
        boolean myId = findMember.isMyId(currentMember.getId());

        MemberProfileResponseDto memberProfileResponseDto = convertMemberToProfileResponseDto(myId, findMember, reviewRecodesCounts, memberPlayInclinationTopN);

        return ResponseEntity.ok(memberProfileResponseDto);
    }

    private MemberProfileResponseDto convertMemberToProfileResponseDto(boolean myId, Member findMember, ReviewRecodesCountsDto reviewRecodesCounts, List<MemberPlayInclination> memberPlayInclinations) {
        if (myId) {
            return MemberMyProfileResponseDto.convert(findMember, reviewRecodesCounts, memberPlayInclinations);
        } else {
            return MemberProfileResponseDto.convert(findMember, reviewRecodesCounts, memberPlayInclinations);
        }
    }

    // TODO: 2021-06-17 추후 방탈출 기록 공개 상태에 따라 요청이 실패할 수 있도록 변경할 수 있음
    // TODO: 2021-06-17 추후 성향에 대한 호칭 기능이 생기면 응답 형태가 변할 수 있음
    /**
     * 문서화 완료, 테스트 완료
     * 기능 테스트
     * - 회원의 플레이 성향이 잘 조회되는지 확인 o
     * - 회원이 생성한 리뷰 개수가 잘 나오는지 확인 o
     *
     * 실페 테스트
     * - 실패 없음
     */
    @GetMapping("/{memberId}/play-inclinations")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<GetMemberPlayInclinationsResponseDto> getMemberPlayInclination(
            @PathVariable Long memberId
    ) {
        List<MemberPlayInclination> memberPlayInclinations = memberService.getMemberPlayInclinations(memberId);
        ReviewRecodesCountsDto reviewRecodesCounts = reviewService.getReviewRecodesCounts(memberId);
        List<MemberPlayInclinationResponseDto> memberPlayInclinationResponseDtos = memberPlayInclinations.stream().map(MemberPlayInclinationResponseDto::convert).collect(Collectors.toList());
        GetMemberPlayInclinationsResponseDto getMemberPlayInclinationsResponseDto = new GetMemberPlayInclinationsResponseDto(memberPlayInclinationResponseDtos, reviewRecodesCounts.getTotalRecodesCount());

        return ResponseEntity.ok(getMemberPlayInclinationsResponseDto);
    }


    @PutMapping("/{memberId}/profiles/images")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> updateProfileImage(
            @PathVariable Long memberId,
            @RequestBody @Valid MemberUpdateProfileImageRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        hasErrorsThrow(ResponseStatus.MEMBER_UPDATE_PROFILE_IMAGE_NOT_VALID, errors);
        ifUpdateDifferentMemberThrows(memberId, currentMember);

        memberService.updateProfileImage(memberId, requestDto.toServiceDto());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{memberId}/profiles/images")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> deleteProfileImage(
            @PathVariable Long memberId,
            @CurrentUser Member currentMember
    ) {
        ifUpdateDifferentMemberThrows(memberId, currentMember);

        memberService.deleteProfileImage(memberId);

        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{memberId}/nicknames")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> updateNickname(
            @PathVariable Long memberId,
            @RequestBody @Valid MemberUpdateNicknameRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        hasErrorsThrow(ResponseStatus.MEMBER_UPDATE_NICKNAME_NOT_VALID, errors);
        ifUpdateDifferentMemberThrows(memberId, currentMember);

        memberService.updateNickname(memberId, requestDto.getNickname());

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{memberId}/descriptions")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> updateDescription(
            @PathVariable Long memberId,
            @RequestBody @Valid MemberUpdateDescriptionRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        hasErrorsThrow(ResponseStatus.MEMBER_UPDATE_DESCRIPTION_NOT_VALID, errors);
        ifUpdateDifferentMemberThrows(memberId, currentMember);

        memberService.updateDescription(memberId, requestDto.getDescription());

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{memberId}/room-escape-recodes-open-status")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> updateRoomEscapeRecodesOpenStatus(
            @PathVariable Long memberId,
            @RequestBody @Valid MemberRoomEscapeRecodesOpenStatusUpdateRequestDto requestDto,
            @CurrentUser Member currentMember
    ) {
        ifUpdateDifferentMemberThrows(memberId, currentMember);
        memberService.updateRoomEscapeRecodesOpenStatus(memberId, requestDto.getRoomEscapeRecodesOpenStatus());

        return ResponseEntity.noContent().build();
    }

    /**
     * 테스트 x, 문서화 x
     *
     * 기능 테스트
     * - 200
     * - 코드, 메세지 확인
     * - 회원이 정상적으로 조회되는지 확인
     *
     * todo: 회원 검색 실패 테스트 구현
     * 실패 테스트
     * - validation - bad request
     * -- 검색 조건 기입 x
     * -- 키워드 기입 x
     *
     * - service
     * - 검색 타입과 키워돌 회원 검색에 실패한 경우 - not found
     *
     * - 인증되지 않은 회원 접근 - 401
     * - 탈퇴된 회원이 접근 - 403
     */
    @PostMapping("/search")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<MemberProfileResponseDto> searchMember(
            @RequestBody @Valid MemberSearchRequestDto requestDto,
            Errors errors
    ) {
        hasErrorsThrow(ResponseStatus.SEARCH_MEMBER_NOT_VALID, errors);
        Member findMember = memberService.searchMember(requestDto.getSearchType(), requestDto.getKeyword());
        ReviewRecodesCountsDto reviewRecodesCounts = reviewService.getReviewRecodesCounts(findMember.getId());
        List<MemberPlayInclination> memberPlayInclinationTopN = memberService.getMemberPlayInclinationTopN(findMember.getId());
        MemberProfileResponseDto memberProfileResponseDto = MemberProfileResponseDto.convert(findMember, reviewRecodesCounts, memberPlayInclinationTopN);

        return ResponseEntity.ok(memberProfileResponseDto);
    }


    // TODO: 2021-05-02 회원 목록 조회 기능 구현(관리자)



    private void ifUpdateDifferentMemberThrows(Long memberId, Member currentMember) {
        if (!currentMember.getId().equals(memberId)) {
            throw new UpdateDifferentMemberException();
        }
    }
}
