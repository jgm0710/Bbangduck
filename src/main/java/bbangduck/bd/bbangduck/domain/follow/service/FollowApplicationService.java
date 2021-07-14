package bbangduck.bd.bbangduck.domain.follow.service;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 팔로우와 관련된 비즈니스 로직을 통합하여 구현한 Application Service
 *
 * @author Gumin Jeong
 * @since 2021-07-14
 */
@Service
@RequiredArgsConstructor
public class FollowApplicationService {

    private final MemberService memberService;

    private final FollowService followService;

    public void requestFollow(Long followingMemberId, Long followedMemberId) {
        Member followingMember = memberService.getMember(followingMemberId);
        Member followedMember = memberService.getMember(followedMemberId);
        followService.follow(followingMember, followedMember);
    }
}
