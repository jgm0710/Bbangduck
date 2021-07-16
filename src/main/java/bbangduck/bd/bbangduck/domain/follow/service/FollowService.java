package bbangduck.bd.bbangduck.domain.follow.service;

import bbangduck.bd.bbangduck.domain.follow.entity.Follow;
import bbangduck.bd.bbangduck.domain.follow.entity.FollowStatus;
import bbangduck.bd.bbangduck.domain.follow.repository.FollowQueryRepository;
import bbangduck.bd.bbangduck.domain.follow.repository.FollowRepository;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


/**
 * 작성자 : Gumin Jeong
 * <p>
 * 작성 일자 : 2021-06-16
 * <p>
 * 팔로우와 관련된 비즈니스 로직을 구현한 Service
 */
@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    private final FollowQueryRepository followQueryRepository;

    @Transactional
    public void follow(Member followingMember, Member followedMember) {
        Follow newFollow = Follow.init(followingMember, followedMember);
        followRepository.findByFollowingMemberAndFollowedMember(followedMember, followingMember).ifPresent(followed -> {
            followed.updateStatus(FollowStatus.TWO_WAY_FOLLOW);
            newFollow.updateStatus(FollowStatus.TWO_WAY_FOLLOW);
        });
        Follow follow = followRepository.findByFollowingMemberAndFollowedMember(followingMember, followedMember).orElse(newFollow);
        followRepository.save(follow);
    }

    @Transactional(readOnly = true)
    public boolean isTwoWayFollowRelationMembers(Long followingMemberId, List<Long> followedMemberIds) {
        List<Follow> findFollows = followQueryRepository.findByFollowingMemberIdAndFollowedMemberIds(followingMemberId, followedMemberIds);
        if (findFollows.size() != followedMemberIds.size()) {
            return false;
        }

        for (Follow findFollow : findFollows) {
            FollowStatus status = findFollow.getStatus();
            if (status != FollowStatus.TWO_WAY_FOLLOW) {
                return false;
            }
        }

        return true;
    }

    @Transactional(readOnly = true)
    public boolean isTwoWayFollowRelation(Long followingMemberId, Long followedMemberId) {
        Optional<Follow> optionalFollow1 = followQueryRepository.findByFollowingMemberIdAndFollowedMemberId(followingMemberId, followedMemberId);
        Optional<Follow> optionalFollow2 = followQueryRepository.findByFollowingMemberIdAndFollowedMemberId(followedMemberId, followingMemberId);

        if (optionalFollow1.isEmpty() || optionalFollow2.isEmpty()) {
            return false;
        } else {
            Follow following = optionalFollow1.get();
            Follow followed = optionalFollow2.get();

            return following.getStatus() == FollowStatus.TWO_WAY_FOLLOW && followed.getStatus() == FollowStatus.TWO_WAY_FOLLOW;
        }
    }

    @Transactional(readOnly = true)
    public List<Follow> getFollowsByFollowingMemberId(Long followingMemberId, CriteriaDto criteria) {
        return followQueryRepository.findListByFollowingMemberId(followingMemberId, criteria);
    }

    @Transactional(readOnly = true)
    public List<Follow> getFollowsByFollowedMemberId(Long followedMemberId, CriteriaDto criteria) {
        return followQueryRepository.findListByFollowedMemberId(followedMemberId, criteria);
    }

    @Transactional
    public void unfollow(Long followingMemberId, Long followedMemberId) {
        followQueryRepository.findByFollowingMemberIdAndFollowedMemberId(followingMemberId, followedMemberId).ifPresent(following -> {
            followRepository.delete(following);
            followQueryRepository.findByFollowingMemberIdAndFollowedMemberId(followedMemberId, followingMemberId)
                    .ifPresent(followed -> followed.updateStatus(FollowStatus.ONE_WAY_FOLLOW));
        });
    }

    @Transactional(readOnly = true)
    public List<Follow> getTwoWayFollowsByMemberId(Long memberId, CriteriaDto criteria) {
        return followQueryRepository.findTwoWayFollowListByFollowingMemberId(memberId, criteria);
    }
}
