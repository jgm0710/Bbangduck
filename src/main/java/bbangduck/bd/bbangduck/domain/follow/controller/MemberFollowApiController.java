package bbangduck.bd.bbangduck.domain.follow.controller;

import bbangduck.bd.bbangduck.domain.follow.dto.controller.response.FollowMemberResponseDto;
import bbangduck.bd.bbangduck.domain.follow.service.FollowApplicationService;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static bbangduck.bd.bbangduck.global.common.ThrowUtils.hasErrorsThrow;

/**
 * 특정 회원과 관련된 팔로우에 대한 EndPoint 를 구현한 Api Controller
 *
 * @author Gumin Jeong
 * @since 2021-07-14
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}")
public class MemberFollowApiController {

    private final FollowApplicationService followApplicationService;
    
    @GetMapping("/followings")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<FollowMemberResponseDto>> getFollowingMemberList(
            @PathVariable Long memberId,
            @ModelAttribute @Valid CriteriaDto criteria,
            BindingResult bindingResult
    ) {
        hasErrorsThrow(ResponseStatus.GET_FOLLOWING_MEMBER_LIST_NOT_VALID, bindingResult);
        List<FollowMemberResponseDto> result = followApplicationService.getFollowingMemberList(memberId, criteria);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/followers")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<FollowMemberResponseDto>> getFollowerMemberList(
            @PathVariable Long memberId,
            @ModelAttribute @Valid CriteriaDto criteria,
            BindingResult bindingResult
    ) {
        hasErrorsThrow(ResponseStatus.GET_FOLLOWER_MEMBER_LIST_NOT_VALID, bindingResult);
        List<FollowMemberResponseDto> result = followApplicationService.getFollowerMemberList(memberId, criteria);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/two-way-followers")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<FollowMemberResponseDto>> getTwoWayFollowMemberList(
            @PathVariable Long memberId,
            @ModelAttribute @Valid CriteriaDto criteria,
            BindingResult bindingResult
    ) {
        hasErrorsThrow(ResponseStatus.GET_TWO_WAY_FOLLOW_MEMBER_LIST_NOT_VALID, bindingResult);
        List<FollowMemberResponseDto> result = followApplicationService.getTwoWayFollowMemberList(memberId, criteria);

        return ResponseEntity.ok(result);
    }

}
