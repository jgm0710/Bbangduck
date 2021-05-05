package bbangduck.bd.bbangduck.domain.member.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.member.dto.MemberDetailDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.global.common.ResponseDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController {

    private final MemberService memberService;

    /**
     * TODO: 21. 5. 5. 회원 조회 기능 테스트 구현
     * 일반 기능 -
     * 회원을 찾을 수 없는 경우
     * 인증 토큰이 유효하지 않은 경우 -
     * 탈퇴한 회원이 리소스에 접근하는 경우
     * 다른 회원의 프로필을 조회하는 경우
     */
    @GetMapping("/{memberId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<MemberDetailDto>> getProfile(@PathVariable Long memberId) {

        Member findMember = memberService.getMember(memberId);
        MemberDetailDto memberDetailDto = MemberDetailDto.convert(findMember);

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.GET_MEMBER_PROFILE_SUCCESS, memberDetailDto));
    }
    // TODO: 2021-05-04 회원 조회 기능 구현 시 인증 및 인가 실패에 대한 테스트 진행

    // TODO: 2021-05-02 회원 수정 기능 구현
    // TODO: 2021-05-02 회원 프로필 이미지 수정 기능 구현
    // TODO: 2021-05-02 회원 프로필 이미지 및 썸네일 이미지 다운로드 기능 구현

    // TODO: 2021-05-02 회원 목록 조회 기능 구현(관리자)
}
