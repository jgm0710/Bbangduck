package bbangduck.bd.bbangduck.domain.member.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.member.dto.MyProfileResponseDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.global.common.ResponseDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // TODO: 2021-05-06 프로필 조회에 방탈출 현황, 성향, 배지 등의 정보 추가로 응답하도록 구현
    // TODO: 2021-05-06 리뷰에 대한 구현이 끝난 뒤 추가 구현
    @GetMapping("/{memberId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<MyProfileResponseDto>> getProfile(
            @PathVariable Long memberId,
            @CurrentUser Member currentMember
    ) {
        log.debug("currentMember : "+currentMember.toString());
        Member findMember = memberService.getMember(memberId);
        MyProfileResponseDto myProfileResponseDto = MyProfileResponseDto.convert(findMember);
        // TODO: 2021-05-05 다른 회원의 프로필을 조회할 경우에 대한 처리 추가
        // TODO: 2021-05-06 다른 회원의 프로필을 조회할 경우 해당 회원의 프로필 공개 여부에 따라 분기 처리 및 별도의 Dto 를 통해 응답

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.GET_MEMBER_PROFILE_SUCCESS, myProfileResponseDto));
    }

    // TODO: 2021-05-02 회원 수정 기능 구현
    // TODO: 2021-05-06 S3 를 통한 파일 업로드 기능 구현
    // TODO: 2021-05-02 회원 프로필 이미지 수정 기능 구현 -> 회원 수정 시 한번에 처리
    // TODO: 2021-05-02 회원 프로필 이미지 및 썸네일 이미지 다운로드 기능 구현

    // TODO: 2021-05-02 회원 목록 조회 기능 구현(관리자)
}
