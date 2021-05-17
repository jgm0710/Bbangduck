package bbangduck.bd.bbangduck.domain.member.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.member.controller.dto.MemberUpdateProfileImageRequestDto;
import bbangduck.bd.bbangduck.domain.member.controller.dto.MemberUpdateProfileRequestDto;
import bbangduck.bd.bbangduck.domain.member.controller.dto.MyProfileResponseDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.exception.UpdateDifferentMemberException;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.global.common.ResponseDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.ThrowUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static bbangduck.bd.bbangduck.global.common.ThrowUtils.hasErrorsThrow;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 관리에 대한 요청을 다루기 위한 Controller
 * 회원 조회, 회원 수정 등
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Slf4j
public class MemberApiController {

    private final MemberService memberService;

    private final MemberValidator memberValidator;

    // TODO: 2021-05-06 프로필 조회에 방탈출 현황, 성향, 배지 등의 정보 추가로 응답하도록 구현
    // TODO: 2021-05-06 리뷰에 대한 구현이 끝난 뒤 추가 구현
    @GetMapping("/{memberId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<MyProfileResponseDto>> getProfile(
            @PathVariable Long memberId,
            @CurrentUser Member currentMember
    ) {
        log.debug("currentMember : {}",currentMember.toString());
        Member findMember = memberService.getMember(memberId);
        MyProfileResponseDto myProfileResponseDto = MyProfileResponseDto.convert(findMember);
        // TODO: 2021-05-05 다른 회원의 프로필을 조회할 경우에 대한 처리 추가
        // TODO: 2021-05-06 다른 회원의 프로필을 조회할 경우 해당 회원의 프로필 공개 여부에 따라 분기 처리 및 별도의 Dto 를 통해 응답

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.GET_MEMBER_PROFILE_SUCCESS, myProfileResponseDto));
    }

    // TODO: 21. 5. 17. 프로필 이미지 수정 api 구현
    @PutMapping("/{memberId}/profiles/images")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity updateProfileImage(
            @PathVariable Long memberId,
            @RequestBody @Valid MemberUpdateProfileImageRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        hasErrorsThrow(ResponseStatus.MEMBER_UPDATE_PROFILE_IMAGE_NOT_VALID, errors);
        if (!currentMember.getId().equals(memberId)) {
            throw new UpdateDifferentMemberException();
        }

        memberService.updateProfileImage(memberId, requestDto.toServiceDto());
        return null;
    }

    // TODO: 21. 5. 17. 프로필 이미지 삭제 api 구현

    // TODO: 21. 5. 17. 회원 닉네임 수정

    // TODO: 21. 5. 17. 회원 자기 소개 수정

    // TODO: 21. 5. 17. 방탈출 기록 공개 여부 수정

    // TODO: 2021-05-02 회원 수정 기능 구현
    // TODO: 2021-05-13 회원 프로필 수정 기능 테스트
    /**
     * 기능 테스트
     * 회원을 찾을 수 없는 경우
     * 다른 회원의 프로필을 수정하는 경우
     * 닉네임, 자기소개 1000자 이상, 프로필 이미지 정보 제대로 기입 x
     * 닉네임 중복
     */
    @PutMapping("/{memberId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<Object>> updateProfile(
            @PathVariable Long memberId,
            @RequestBody @Valid MemberUpdateProfileRequestDto memberUpdateProfileRequestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        if (!currentMember.getId().equals(memberId)) {
            throw new UpdateDifferentMemberException();
        }
        memberValidator.validateUpdateProfile(memberUpdateProfileRequestDto, errors);

        memberService.updateMember(memberId, memberUpdateProfileRequestDto.toServiceDto());

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.MEMBER_MODIFY_PROFILE_SUCCESS,null));
    }

    // TODO: 21. 5. 17. 회원 프로필 사진 삭제 기능 구현

    // TODO: 2021-05-02 회원 목록 조회 기능 구현(관리자)
}
