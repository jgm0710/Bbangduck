package bbangduck.bd.bbangduck.domain.member.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.member.dto.controller.request.MemberRoomEscapeRecodesOpenStatusUpdateRequestDto;
import bbangduck.bd.bbangduck.domain.member.dto.controller.request.MemberUpdateDescriptionRequestDto;
import bbangduck.bd.bbangduck.domain.member.dto.controller.request.MemberUpdateNicknameRequestDto;
import bbangduck.bd.bbangduck.domain.member.dto.controller.request.MemberUpdateProfileImageRequestDto;
import bbangduck.bd.bbangduck.domain.member.dto.controller.response.MyProfileResponseDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.exception.UpdateDifferentMemberException;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.global.common.ResponseDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    // TODO: 2021-05-06 프로필 조회에 방탈출 현황, 성향, 배지 등의 정보 추가로 응답하도록 구현
    // TODO: 2021-05-06 리뷰에 대한 구현이 끝난 뒤 추가 구현
    /**
     * 기능 테스트
     * - 다른 회원의 프로필을 조회할 경우
     * -- 회원 ID
     * -- 프로필 이미지
     * -- 닉네임
     * -- 자기소개
     * -- 방탈출 현황
     * -- 방탈출 기록 공개 상태
     *
     * - 자신의 프로필을 조회할 경우
     * -- 회원 ID
     * -- 프로필 이미지
     * -- 닉네임
     * -- 자기소개
     * -- 방탈출 현황
     * -- 이메일
     * -- 어떤 소셜 계정으로 가입된 회원인지
     * -- 생성 일자
     * -- 개인정보 수정 일자
     *
     * 실패 테스트
     * - 조회된 회원이 탈퇴하거나, 계정이 정지된 회원일 경우 조회 불가
     * - 인증되지 않은 회원일 경우 리소스 접근 불가
     * - 탈퇴한 회원일 경우 리소스 접근 불가
     */
    @GetMapping("/{memberId}/profiles")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<MyProfileResponseDto>> getProfile(
            @PathVariable Long memberId,
            @CurrentUser Member currentMember
    ) {
        log.debug("currentMember : {}", currentMember.toString());
        Member findMember = memberService.getMember(memberId);
        if (currentMember.getId().equals(memberId)) {

        } else {

        }
        MyProfileResponseDto myProfileResponseDto = MyProfileResponseDto.convert(findMember);
        // TODO: 2021-05-05 다른 회원의 프로필을 조회할 경우에 대한 처리 추가
        // TODO: 2021-05-06 다른 회원의 프로필을 조회할 경우 해당 회원의 프로필 공개 여부에 따라 분기 처리 및 별도의 Dto 를 통해 응답

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.MEMBER_GET_PROFILE_SUCCESS, myProfileResponseDto));
    }

    @PutMapping("/{memberId}/profiles/images")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<Object>> updateProfileImage(
            @PathVariable Long memberId,
            @RequestBody @Valid MemberUpdateProfileImageRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        hasErrorsThrow(ResponseStatus.MEMBER_UPDATE_PROFILE_IMAGE_NOT_VALID, errors);
        ifUpdateDifferentMemberThrows(memberId, currentMember);

        memberService.updateProfileImage(memberId, requestDto.toServiceDto());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ResponseDto<>(ResponseStatus.MEMBER_UPDATE_PROFILE_IMAGE_SUCCESS, null));
    }

    @DeleteMapping("/{memberId}/profiles/images")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<Object>> deleteProfileImage(
            @PathVariable Long memberId,
            @CurrentUser Member currentMember
    ) {
        ifUpdateDifferentMemberThrows(memberId, currentMember);

        memberService.deleteProfileImage(memberId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ResponseDto<>(ResponseStatus.MEMBER_DELETE_PROFILE_IMAGE_SUCCESS, null));
    }


    @PutMapping("/{memberId}/nicknames")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<Object>> updateNickname(
            @PathVariable Long memberId,
            @RequestBody @Valid MemberUpdateNicknameRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        hasErrorsThrow(ResponseStatus.MEMBER_UPDATE_NICKNAME_NOT_VALID, errors);
        ifUpdateDifferentMemberThrows(memberId, currentMember);

        memberService.updateNickname(memberId, requestDto.getNickname());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ResponseDto<>(ResponseStatus.MEMBER_UPDATE_NICKNAME_SUCCESS, null));
    }

    @PutMapping("/{memberId}/descriptions")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<Object>> updateDescription(
            @PathVariable Long memberId,
            @RequestBody @Valid MemberUpdateDescriptionRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        hasErrorsThrow(ResponseStatus.MEMBER_UPDATE_DESCRIPTION_NOT_VALID, errors);
        ifUpdateDifferentMemberThrows(memberId, currentMember);

        memberService.updateDescription(memberId, requestDto.getDescription());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ResponseDto<>(ResponseStatus.MEMBER_UPDATE_DESCRIPTION_SUCCESS, null));
    }

    @PutMapping("/{memberId}/room-escape-recodes-open-status")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<Object>> updateRoomEscapeRecodesOpenStatus(
            @PathVariable Long memberId,
            @RequestBody @Valid MemberRoomEscapeRecodesOpenStatusUpdateRequestDto requestDto,
            @CurrentUser Member currentMember
    ) {
        ifUpdateDifferentMemberThrows(memberId, currentMember);
        memberService.updateRoomEscapeRecodesOpenStatus(memberId, requestDto.getRoomEscapeRecodesOpenStatus());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ResponseDto<>(ResponseStatus.MEMBER_TOGGLE_ROOM_ESCAPE_RECODES_OPEN_SUCCESS, null));
    }



    // TODO: 2021-05-02 회원 목록 조회 기능 구현(관리자)



    private void ifUpdateDifferentMemberThrows(Long memberId, Member currentMember) {
        if (!currentMember.getId().equals(memberId)) {
            throw new UpdateDifferentMemberException();
        }
    }
}
