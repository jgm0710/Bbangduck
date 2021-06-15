package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.review.dto.controller.request.MemberReviewSearchRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원과 관련된 리뷰 요청 API 를 구현하기 위한 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}/reviews")
public class MemberReviewApiController {

    private final MemberService memberService;

    // TODO: 2021-05-22 특정 회원이 작성한 리뷰 목록 기능 구현
    @GetMapping
    public ResponseEntity getReviewListByMember(
            @PathVariable Long memberId,
            @ModelAttribute @Valid MemberReviewSearchRequestDto requestDto,
            BindingResult bindingResult,
            @CurrentUser Member currentMember
    ) {
        Member findMember = memberService.getMember(memberId);
        boolean roomEscapeRecordsOpenYN = findMember.isRoomEscapeRecordsOpenYN();
        return null;
    }

}
