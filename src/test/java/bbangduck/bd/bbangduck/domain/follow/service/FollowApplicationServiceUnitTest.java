package bbangduck.bd.bbangduck.domain.follow.service;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

/**
 * @author Gumin Jeong
 * @since 2021-07-15
 */
@DisplayName("FollowApplicationService 단위 테스트")
class FollowApplicationServiceUnitTest {

    MemberService memberService = mock(MemberService.class);
    FollowService followService = mock(FollowService.class);

    FollowApplicationService followApplicationService = new FollowApplicationService(memberService, followService);

    @Test
    @DisplayName("팔로우 요청")
    public void requestFollow() {
        //given
        Member followingMember = Member.builder()
                .id(1L)
                .build();

        Member followedMember = Member.builder()
                .id(2L)
                .build();

        given(memberService.getMember(followingMember.getId())).willReturn(followingMember);
        given(memberService.getMember(followedMember.getId())).willReturn(followedMember);

        //when
        followApplicationService.requestFollow(followingMember.getId(), followedMember.getId());

        //then
        then(memberService).should(times(1)).getMember(followingMember.getId());
        then(memberService).should(times(1)).getMember(followedMember.getId());
        then(followService).should(times(1)).follow(followingMember, followedMember);
    }

}