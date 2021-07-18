package bbangduck.bd.bbangduck.domain.follow.service;

import bbangduck.bd.bbangduck.domain.follow.dto.controller.response.FollowMemberResponseDto;
import bbangduck.bd.bbangduck.domain.follow.entity.Follow;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import bbangduck.bd.bbangduck.global.common.PaginationResultResponseDto;
import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void requestFollow(Long followingMemberId, Long followedMemberId) {
        Member followingMember = memberService.getMember(followingMemberId);
        Member followedMember = memberService.getMember(followedMemberId);
        followService.follow(followingMember, followedMember);
    }

    @Transactional(readOnly = true)
    public PaginationResultResponseDto<FollowMemberResponseDto> getFollowingMemberList(Long memberId, CriteriaDto criteria) {
        memberService.getMember(memberId);
        QueryResults<Follow> followsQueryResults = followService.getFollowsByFollowingMemberId(memberId, criteria);
        List<Member> followedMembers = followsQueryResults.getResults().stream()
                .map(Follow::getFollowedMember).collect(Collectors.toList());

        PaginationResultResponseDto<Member> resultResponseDto = new PaginationResultResponseDto<>(followedMembers,
                criteria.getPageNum(),
                criteria.getAmount(),
                followsQueryResults.getTotal());

        return resultResponseDto.convert(FollowMemberResponseDto::convert);
    }

    @Transactional(readOnly = true)
    public PaginationResultResponseDto<FollowMemberResponseDto> getFollowerMemberList(Long memberId, CriteriaDto criteria) {
        memberService.getMember(memberId);
        QueryResults<Follow> followedQueryResults = followService.getFollowsByFollowedMemberId(memberId, criteria);

        List<Member> followers = followedQueryResults.getResults().stream()
                .map(Follow::getFollowingMember).collect(Collectors.toList());

        PaginationResultResponseDto<Member> resultResponseDto = new PaginationResultResponseDto<>(followers,
                criteria.getPageNum(),
                criteria.getAmount(),
                followedQueryResults.getTotal());

        return resultResponseDto.convert(FollowMemberResponseDto::convert);
    }

    @Transactional
    public void unfollow(Long followingMemberId, Long followedMemberId) {
        followService.unfollow(followingMemberId, followedMemberId);
    }

    @Transactional(readOnly = true)
    public List<FollowMemberResponseDto> getTwoWayFollowMemberList(Long memberId, CriteriaDto criteria) {
        memberService.getMember(memberId);
        List<Follow> findFollows = followService.getTwoWayFollowsByMemberId(memberId, criteria);
        return findFollows.stream().map(follow -> FollowMemberResponseDto.convert(follow.getFollowedMember())).collect(Collectors.toList());
    }
}
