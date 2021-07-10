package bbangduck.bd.bbangduck.domain.friend.service;

import bbangduck.bd.bbangduck.domain.friend.entity.MemberFriend;
import bbangduck.bd.bbangduck.domain.friend.exception.RelationOfMemberAndFriendIsNotFriendException;
import bbangduck.bd.bbangduck.domain.friend.repository.MemberFriendQueryRepository;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static bbangduck.bd.bbangduck.global.common.NullCheckUtils.existsList;


/**
 * 작성자 : Gumin Jeong
 * <p>
 * 작성 일자 : 2021-06-16
 * <p>
 * 회원 친구와 관련된 비즈니스 로직을 구현한 Service
 *
 * todo : 친구 기능 구현 시 다듬기
 */
@Service
@RequiredArgsConstructor
public class MemberFriendService {

    private final MemberFriendQueryRepository memberFriendQueryRepository;

    public boolean isFriend(Long memberId, Long friendId) {
        return memberFriendQueryRepository.findAcceptedMemberFriendByMemberIdAndFriendId(memberId, friendId).isPresent();
    }

    // TODO: 2021-07-11 inQuery 로 한번에 조회해오도록 변경
    @Transactional(readOnly = true)
    public List<Member> getAcceptedFriends(Long memberId, List<Long> friendIds) {
        if (!existsList(friendIds)) {
            return null;
        }

        return friendIds.stream()
                .map(friendId -> {
                    MemberFriend memberFriend = memberFriendQueryRepository.findAcceptedMemberFriendByMemberIdAndFriendId(memberId, friendId)
                            .orElseThrow(() -> new RelationOfMemberAndFriendIsNotFriendException(memberId, friendId));
                    return memberFriend.getFriend();
                })
                .collect(Collectors.toList());
    }
}
