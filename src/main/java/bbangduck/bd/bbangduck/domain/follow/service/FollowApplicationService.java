package bbangduck.bd.bbangduck.domain.follow.service;

import bbangduck.bd.bbangduck.domain.follow.dto.controller.response.FollowMemberResponseDto;
import bbangduck.bd.bbangduck.domain.follow.entity.Follow;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<FollowMemberResponseDto> getFollowingMemberList(Long memberId, CriteriaDto criteria) {
        memberService.getMember(memberId);
        List<Follow> followingList = followService.getFollowsByFollowingMemberId(memberId, criteria);
        List<Member> followedMembers = followingList.stream().map(Follow::getFollowedMember).collect(Collectors.toList());
        return followedMembers.stream().map(FollowMemberResponseDto::convert).collect(Collectors.toList());
    }

    public List<FollowMemberResponseDto> getFollowerMemberList(Long memberId, CriteriaDto criteria) {
        memberService.getMember(memberId);
        List<Follow> followedList = followService.getFollowsByFollowedMemberId(memberId, criteria);
        List<Member> followingMembers = followedList.stream().map(Follow::getFollowingMember).collect(Collectors.toList());
        return followingMembers.stream().map(FollowMemberResponseDto::convert).collect(Collectors.toList());
    }
}
