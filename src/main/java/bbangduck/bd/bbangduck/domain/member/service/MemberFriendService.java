package bbangduck.bd.bbangduck.domain.member.service;

import bbangduck.bd.bbangduck.domain.member.repository.MemberFriendQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * 작성자 : Gumin Jeong
 * <p>
 * 작성 일자 : 2021-06-16
 * <p>
 * 회원 친구와 관련된 비즈니스 로직을 구현한 Service
 */
@Service
@RequiredArgsConstructor
public class MemberFriendService {

    private final MemberFriendQueryRepository memberFriendQueryRepository;

    public boolean isFriend(Long memberId, Long friendId) {
        return memberFriendQueryRepository.findAcceptedFriendByMemberAndFriend(memberId, friendId).isPresent();
    }
}
