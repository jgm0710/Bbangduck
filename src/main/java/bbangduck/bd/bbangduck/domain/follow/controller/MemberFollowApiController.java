package bbangduck.bd.bbangduck.domain.follow.controller;

import bbangduck.bd.bbangduck.domain.follow.service.FollowApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // TODO: 2021-07-14 특전 회원의 팔로윙 목록 조회 기능 구현
    @GetMapping("/followings")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity getFollowList(
            @PathVariable Long memberId
    ) {
        return null;
    }

    // TODO: 2021-07-14 특정 회원의 팔로워 목록 조회 기능 구현
    @GetMapping("/followers")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity getFollowedList(
            @PathVariable Long memberId
    ) {
        return null;
    }
}
