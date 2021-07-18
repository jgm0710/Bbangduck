package bbangduck.bd.bbangduck.domain.follow.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.follow.dto.controller.response.FollowMemberResponseDto;
import bbangduck.bd.bbangduck.domain.follow.service.FollowApplicationService;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import bbangduck.bd.bbangduck.global.common.PaginationResultResponseDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static bbangduck.bd.bbangduck.global.common.ThrowUtils.hasErrorsThrow;

/**
 * 팔로우와 관련된 EndPoint 를 구현한 Api Controller
 *
 * @author Gumin Jeong
 * @since 2021-07-14
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}")
public class FollowApiController {

    private final FollowApplicationService followApplicationService;

    @PostMapping("/follows")
    @PreAuthorize("hasRole('ROLE_USER')")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    public void requestFollow(
            @PathVariable("memberId") Long followedMemberId,
            @CurrentUser Member currentMember
    ) {
        followApplicationService.requestFollow(currentMember.getId(), followedMemberId);
    }

    @GetMapping("/followings")
    @PreAuthorize("hasRole('ROLE_USER')")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.OK)
    public PaginationResultResponseDto<FollowMemberResponseDto> getFollowingMemberList(
            @PathVariable Long memberId,
            @ModelAttribute @Valid CriteriaDto criteria,
            BindingResult bindingResult
    ) {
        hasErrorsThrow(bbangduck.bd.bbangduck.global.common.ResponseStatus.GET_FOLLOWING_MEMBER_LIST_NOT_VALID, bindingResult);
        return followApplicationService.getFollowingMemberList(memberId, criteria);
    }

    @GetMapping("/followers")
    @PreAuthorize("hasRole('ROLE_USER')")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.OK)
    public PaginationResultResponseDto<FollowMemberResponseDto> getFollowerMemberList(
            @PathVariable Long memberId,
            @ModelAttribute @Valid CriteriaDto criteria,
            BindingResult bindingResult
    ) {
        hasErrorsThrow(bbangduck.bd.bbangduck.global.common.ResponseStatus.GET_FOLLOWER_MEMBER_LIST_NOT_VALID, bindingResult);
        return followApplicationService.getFollowerMemberList(memberId, criteria);
    }

    @GetMapping("/two-way-followers")
    @PreAuthorize("hasRole('ROLE_USER')")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.OK)
    public List<FollowMemberResponseDto> getTwoWayFollowMemberList(
            @PathVariable Long memberId,
            @ModelAttribute @Valid CriteriaDto criteria,
            BindingResult bindingResult
    ) {
        hasErrorsThrow(ResponseStatus.GET_TWO_WAY_FOLLOW_MEMBER_LIST_NOT_VALID, bindingResult);
        return followApplicationService.getTwoWayFollowMemberList(memberId, criteria);
    }

    @DeleteMapping("/follows")
    @PreAuthorize("hasRole('ROLE_USER')")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollow(
            @PathVariable("memberId") Long followedMemberId,
            @CurrentUser Member currentMember
    ) {
        followApplicationService.unfollow(currentMember.getId(), followedMemberId);
    }
}
