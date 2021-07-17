package bbangduck.bd.bbangduck.domain.follow.service;

import bbangduck.bd.bbangduck.domain.follow.entity.Follow;
import bbangduck.bd.bbangduck.domain.follow.exception.AlreadyFollowMemberException;
import bbangduck.bd.bbangduck.domain.follow.exception.FollowNotFoundException;
import bbangduck.bd.bbangduck.domain.follow.repository.FollowQueryRepository;
import bbangduck.bd.bbangduck.domain.follow.repository.FollowRepository;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

class FollowServiceUnitTest {

    FollowRepository followRepository = Mockito.mock(FollowRepository.class);
    FollowQueryRepository followQueryRepository = Mockito.mock(FollowQueryRepository.class);

    FollowService followService = new FollowService(followRepository, followQueryRepository);

    @Test
    @DisplayName("팔로우 요청 - 이미 팔로우한 회원일 경우")
    public void follow_alreadyFollowMember() {
        //given
        Member followingMember = Member.builder()
                .id(1L)
                .build();

        Member followedMember = Member.builder()
                .id(2L)
                .build();

        Follow follow = Follow.builder()
                .id(1L)
                .followingMember(followingMember)
                .followedMember(followedMember)
                .build();

        given(followRepository.findByFollowingMemberAndFollowedMember(followingMember, followedMember)).willReturn(Optional.of(follow));

        //when

        //then
        assertThrows(AlreadyFollowMemberException.class, () -> followService.follow(followingMember, followedMember));

    }

    @Test
    @DisplayName("팔로우 해제 - 팔로우를 찾을 수 없는 경우")
    public void unfollow_FollowNotFound() {
        //given
        given(followQueryRepository.findByFollowingMemberIdAndFollowedMemberId(anyLong(), anyLong())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(FollowNotFoundException.class, () -> followService.unfollow(1L, 2L));
    }

}