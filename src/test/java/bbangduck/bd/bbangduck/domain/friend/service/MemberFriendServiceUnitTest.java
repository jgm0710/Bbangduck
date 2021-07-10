package bbangduck.bd.bbangduck.domain.friend.service;

import bbangduck.bd.bbangduck.domain.friend.exception.RelationOfMemberAndFriendIsNotFriendException;
import bbangduck.bd.bbangduck.domain.friend.repository.MemberFriendQueryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

@DisplayName("MemberFriendService 단위 테스트")
class MemberFriendServiceUnitTest {

    MemberFriendQueryRepository memberFriendQueryRepository = Mockito.mock(MemberFriendQueryRepository.class);

    MemberFriendService memberFriendService = new MemberFriendService(memberFriendQueryRepository);

    @Test
    @DisplayName("친구 ID 목록을 통한 회원 친구 목록 조회 - 친구가 아닌 경우")
    public void getAcceptedFriends_RelationOfMemberAndFriendIsNotFriend() {
        //given
        long memberId = 1L;
        List<Long> friendIds = List.of(2L, 3L, 4L);

        given(memberFriendQueryRepository.findAcceptedMemberFriendByMemberIdAndFriendId(memberId, friendIds.get(0))).willReturn(Optional.empty());

        //when

        //then
        Assertions.assertThrows(RelationOfMemberAndFriendIsNotFriendException.class, () -> memberFriendService.getAcceptedFriends(memberId, friendIds));

    }

}