package bbangduck.bd.bbangduck.domain.follow.service;

import bbangduck.bd.bbangduck.domain.follow.entity.Follow;
import bbangduck.bd.bbangduck.domain.follow.repository.FollowQueryRepository;
import bbangduck.bd.bbangduck.domain.follow.repository.FollowRepository;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


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
        Follow follow = followRepository.findByFollowingMemberAndFollowedMember(followingMember, followedMember).orElse(Follow.init(followingMember, followedMember));
        followRepository.save(follow);
    }

    public boolean isTwoWayFollowRelationMembers(Long followingMemberId, List<Long> followedMemberIds) {
        List<Follow> follows1 = followQueryRepository.findByFollowingMemberIdAndFollowedMemberIds(followingMemberId, followedMemberIds);
        List<Follow> follows2 = followQueryRepository.findByFollowingMemberIdsAndFollowedMemberId(followedMemberIds, followingMemberId);

        return follows1.size() == follows2.size();
    }

    public boolean isTwoWayFollowRelation(Long followingMemberId, Long followedMemberId) {
        boolean present1 = followQueryRepository.findByFollowingMemberIdAndFollowedMemberId(followingMemberId, followedMemberId).isPresent();
        boolean present2 = followQueryRepository.findByFollowingMemberIdAndFollowedMemberId(followedMemberId, followingMemberId).isPresent();

        return present1 && present2;
    }

    public List<Follow> getFollowsByFollowingMemberId(Long followingMemberId, CriteriaDto criteria) {
        return followQueryRepository.findListByFollowingMemberId(followingMemberId, criteria);
    }

    public List<Follow> getFollowsByFollowedMemberId(Long followedMemberId, CriteriaDto criteria) {
        return followQueryRepository.findListByFollowedMemberId(followedMemberId, criteria);
    }
}
