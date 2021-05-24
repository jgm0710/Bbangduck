package bbangduck.bd.bbangduck.domain.member.controller;

import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSignInRequestDto;
import bbangduck.bd.bbangduck.domain.auth.service.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.service.MemberDevelopService;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import bbangduck.bd.bbangduck.global.common.ResponseDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 프론트 개발 시점에 필요한 편의 기능을 제공하기 위해 구현한 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/develop/members")
public class MemberDevelopApiController {

    private final MemberDevelopService memberDevelopService;

    private final MemberService memberService;

    @PostMapping("/sign-in")
    public ResponseEntity<ResponseDto<TokenDto>> signInDeveloper(
            @RequestBody MemberSignInRequestDto requestDto
    ) {
        TokenDto tokenDto = memberDevelopService.signInDeveloper(requestDto.toServiceDto());

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SIGN_IN_DEVELOPER_SUCCESS, tokenDto));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_DEVELOP')")
    public ResponseEntity<ResponseDto<List<Member>>> getMemberListByDeveloper(
            @ModelAttribute CriteriaDto criteriaDto
    ) {
        List<Member> memberList = memberDevelopService.getMemberList(criteriaDto);
        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.GET_MEMBER_LIST_BY_DEVELOPER_SUCCESS, memberList));
    }

    @GetMapping("/{memberId}")
    @PreAuthorize("hasRole('ROLE_DEVELOP')")
    public ResponseEntity<ResponseDto<Member>> getMemberByDeveloper(
            @PathVariable Long memberId
    ) {
        Member member = memberService.getMember(memberId);

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.GET_MEMBER_BY_DEVELOPER_SUCCESS, member));
    }

    @DeleteMapping("/{memberId}")
    @PreAuthorize("hasRole('ROLE_DEVELOP')")
    public ResponseEntity<ResponseDto<Object>> deleteMemberByDeveloper(
            @PathVariable Long memberId
    ) {
        memberDevelopService.deleteMember(memberId);

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.DELETE_MEMBER_SUCCESS_BY_DEVELOPER_SUCCESS, null));
    }

}
