package bbangduck.bd.bbangduck.domain.follow.service;

import bbangduck.bd.bbangduck.domain.follow.dto.controller.response.FollowMemberResponseDto;
import bbangduck.bd.bbangduck.domain.follow.entity.Follow;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberProfileImage;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @Test
    @DisplayName("팔로우 하는 회원 목록 조회")
    public void getFollowingMemberList() {
        //given
        Member member = Member.builder()
                .id(1L)
                .build();

        List<Member> followedMembers = new ArrayList<>();
        List<Follow> follows = new ArrayList<>();
        for (long i = 2; i < 6; i++) {
            Member followedMember = Member.builder()
                    .id(i)
                    .nickname("member"+i)
                    .description("description"+i)
                    .build();

            int nextInt = new Random().nextInt(100);
            MemberProfileImage memberProfileImage = MemberProfileImage.builder()
                    .id(i)
                    .fileStorageId((long) nextInt)
                    .fileName("fileName" + nextInt)
                    .build();

            followedMember.setProfileImage(memberProfileImage);

            followedMembers.add(followedMember);

            Follow follow = Follow.builder()
                    .followingMember(member)
                    .followedMember(followedMember)
                    .build();

            follows.add(follow);
        }

        CriteriaDto criteriaDto = new CriteriaDto();

        given(memberService.getMember(member.getId())).willReturn(member);
        given(followService.getFollowingList(member.getId(), criteriaDto)).willReturn(follows);

        //when
        List<FollowMemberResponseDto> followingMemberList = followApplicationService.getFollowingMemberList(member.getId(), criteriaDto);

        //then
        then(memberService).should(times(1)).getMember(member.getId());
        then(followService).should(times(1)).getFollowingList(member.getId(), criteriaDto);

        followingMemberList.forEach(followMemberResponseDto -> {
            System.out.println("followMemberResponseDto = " + followMemberResponseDto);
            assertNotNull(followMemberResponseDto.getMemberId());
            assertNotNull(followMemberResponseDto.getNickname());
            assertNotNull(followMemberResponseDto.getDescription());
            assertNotNull(followMemberResponseDto.getProfileImageUrl());
            assertNotNull(followMemberResponseDto.getProfileImageThumbnailUrl());
        });

    }

}