package bbangduck.bd.bbangduck.domain.follow.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.follow.service.FollowApplicationService;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 팔로우와 관련된 EndPoint 를 구현한 Api Controller
 *
 * @author Gumin Jeong
 * @since 2021-07-14
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")
public class FollowApiController {

    private final FollowApplicationService followApplicationService;

    @PostMapping("/{memberId}/request-follow")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> requestFollow(
            @PathVariable("memberId") Long followedMemberId,
            @CurrentUser Member currentMember
    ) {
        followApplicationService.requestFollow(currentMember.getId(), followedMemberId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{memberId}/unfollow")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> unfollow(
            @PathVariable("memberId") Long followedMemberId,
            @CurrentUser Member currentMember
    ) {
        followApplicationService.unfollow(currentMember.getId(), followedMemberId);

        return ResponseEntity.ok().build();
    }
}
