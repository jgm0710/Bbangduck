package bbangduck.bd.bbangduck.domain.member.controller;

import bbangduck.bd.bbangduck.domain.auth.dto.controller.MemberSignInRequestDto;
import bbangduck.bd.bbangduck.domain.auth.dto.service.TokenDto;
import bbangduck.bd.bbangduck.domain.member.dto.controller.response.MemberMyProfileResponseDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberPlayInclination;
import bbangduck.bd.bbangduck.domain.member.service.MemberDevelopService;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.review.dto.entity.ReviewRecodesCountsDto;
import bbangduck.bd.bbangduck.domain.review.service.ReviewService;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import bbangduck.bd.bbangduck.global.common.ResponseDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    private final ReviewService reviewService;

    @PostMapping("/sign-in")
    public ResponseEntity<ResponseDto<TokenDto>> signInDeveloper(
            @RequestBody MemberSignInRequestDto requestDto
    ) {
        TokenDto tokenDto = memberDevelopService.signInDeveloper(requestDto.toServiceDto());

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SIGN_IN_DEVELOPER_SUCCESS, tokenDto));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_DEVELOP')")
    public ResponseEntity<ResponseDto<List<MemberMyProfileResponseDto>>> getMemberListByDeveloper(
            @ModelAttribute CriteriaDto criteriaDto
    ) {
        List<Member> memberList = memberDevelopService.getMemberList(criteriaDto);
        List<MemberMyProfileResponseDto> memberMyProfileResponseDtos = new ArrayList<>();
        memberList.forEach(member -> {
            ReviewRecodesCountsDto reviewRecodesCounts = reviewService.getReviewRecodesCounts(member.getId());
            List<MemberPlayInclination> memberPlayInclinationTopN = memberService.getMemberPlayInclinationTopN(member.getId());
            MemberMyProfileResponseDto memberMyProfileResponseDto = MemberMyProfileResponseDto.convert(member, reviewRecodesCounts, memberPlayInclinationTopN);
            memberMyProfileResponseDtos.add(memberMyProfileResponseDto);
        });
        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.GET_MEMBER_LIST_BY_DEVELOPER_SUCCESS, memberMyProfileResponseDtos));
    }

    @GetMapping("/{memberId}")
    @PreAuthorize("hasRole('ROLE_DEVELOP')")
    public ResponseEntity<ResponseDto<MemberMyProfileResponseDto>> getMemberByDeveloper(
            @PathVariable Long memberId
    ) {
        Member member = memberDevelopService.getMemberByDeveloper(memberId);
        List<MemberPlayInclination> memberPlayInclinationTopN = memberService.getMemberPlayInclinationTopN(memberId);
        ReviewRecodesCountsDto reviewRecodesCounts = reviewService.getReviewRecodesCounts(memberId);
        MemberMyProfileResponseDto memberMyProfileResponseDto = MemberMyProfileResponseDto.convert(member, reviewRecodesCounts, memberPlayInclinationTopN);

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.GET_MEMBER_BY_DEVELOPER_SUCCESS, memberMyProfileResponseDto));
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
