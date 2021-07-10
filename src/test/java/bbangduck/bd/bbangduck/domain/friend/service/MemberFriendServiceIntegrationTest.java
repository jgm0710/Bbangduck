package bbangduck.bd.bbangduck.domain.friend.service;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.friend.entity.MemberFriend;
import bbangduck.bd.bbangduck.domain.friend.enumerate.MemberFriendState;
import bbangduck.bd.bbangduck.domain.friend.repository.MemberFriendRepository;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("MemberFriendService 통합 테스트")
class MemberFriendServiceIntegrationTest extends BaseTest {

    @Autowired
    MemberFriendService memberFriendService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberFriendRepository memberFriendRepository;

    @Test
    @DisplayName("친구 ID 목록을 통해 회원 친구 목록 조회")
    public void getAcceptedFriends() {
        //given
        Member member = Member.builder()
                .email("gafmember@email.com")
                .nickname("gafmember")
                .build();

        memberRepository.save(member);

        List<Member> friends = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Member friend = Member.builder()
                    .email("gaffriend" + i + "@email.com")
                    .nickname("gaffriend" + i)
                    .build();
            memberRepository.save(friend);

            MemberFriend memberFriend = MemberFriend.builder()
                    .member(member)
                    .friend(friend)
                    .state(MemberFriendState.ACCEPT)
                    .build();
            memberFriendRepository.save(memberFriend);

            friends.add(friend);
        }

        List<Long> friendIds = friends.stream().map(Member::getId).collect(Collectors.toList());

        //when
        List<Member> acceptedFriends = memberFriendService.getAcceptedFriends(member.getId(), friendIds);

        //then
        assertEquals(3, acceptedFriends.size(), "조회된 친구의 수는 3명 이어야 한다.");

        acceptedFriends.forEach(friend -> {
            Long friendId = friend.getId();
            boolean anyMatch = friendIds.stream().anyMatch(aLong -> aLong.equals(friendId));
            assertTrue(anyMatch, "조회된 친구 목록 중 하나의 ID 는 조회 시 입력한 ID 중 하나와 일치해야 한다.");
        });

    }

}