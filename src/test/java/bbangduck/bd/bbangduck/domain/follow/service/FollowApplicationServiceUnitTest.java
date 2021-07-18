package bbangduck.bd.bbangduck.domain.follow.service;

import bbangduck.bd.bbangduck.domain.follow.dto.controller.response.FollowMemberResponseDto;
import bbangduck.bd.bbangduck.domain.follow.entity.Follow;
import bbangduck.bd.bbangduck.domain.follow.entity.FollowStatus;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberProfileImage;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import bbangduck.bd.bbangduck.global.common.PaginationResultResponseDto;
import com.querydsl.core.QueryResults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
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
                    .nickname("member" + i)
                    .description("description" + i)
                    .build();

            int nextInt = new Random().nextInt(100);
            MemberProfileImage memberProfileImage = MemberProfileImage.builder()
                    .id(i)
                    .fileStorageId((long) nextInt)
                    .fileName("fileName" + nextInt)
                    .build();

            followedMember.setProfileImage(memberProfileImage);

            followedMembers.add(followedMember);

            Follow follow = Follow.init(member, followedMember);

            follows.add(follow);
        }

        CriteriaDto criteriaDto = new CriteriaDto();

        given(memberService.getMember(member.getId())).willReturn(member);
        QueryResults<Follow> followQueryResults = new QueryResults<>(follows, (long) criteriaDto.getAmount(), (long) criteriaDto.getOffset(), follows.size());
        given(followService.getFollowsByFollowingMemberId(member.getId(), criteriaDto)).willReturn(followQueryResults);

        //when
        PaginationResultResponseDto<FollowMemberResponseDto> resultResponseDto = followApplicationService.getFollowingMemberList(member.getId(), criteriaDto);

        //then
        then(memberService).should(times(1)).getMember(member.getId());
        then(followService).should(times(1)).getFollowsByFollowingMemberId(member.getId(), criteriaDto);

        List<FollowMemberResponseDto> followingMemberList = resultResponseDto.getContents();

        followingMemberList.forEach(followMemberResponseDto -> {
            System.out.println("followMemberResponseDto = " + followMemberResponseDto);
            assertNotNull(followMemberResponseDto.getMemberId());
            assertNotNull(followMemberResponseDto.getNickname());
            assertNotNull(followMemberResponseDto.getDescription());
            assertNotNull(followMemberResponseDto.getProfileImageUrl());
            assertNotNull(followMemberResponseDto.getProfileImageThumbnailUrl());

            assertTrue(followedMembers.stream().anyMatch(member1 -> member1.getId().equals(followMemberResponseDto.getMemberId())),
                    "조회되는 회원의 식별 ID 중 하나는 member 가 팔로우하는 회원의 식별 ID 중 하나여야한다.");
        });

        assertEquals(criteriaDto.getPageNum(),resultResponseDto.getNowPageNum());
        assertEquals(criteriaDto.getAmount(), resultResponseDto.getRequestAmount());
        assertEquals(follows.size(), resultResponseDto.getTotalResultsCount());

    }

    @Test
    @DisplayName("특정 회원을 팔로우하는 회원 목록 조회")
    public void getFollowerMemberList() {
        //given
        Member followedMember = Member.builder()
                .id(1000L)
                .build();

        List<Member> followingMembers = new ArrayList<>();
        List<Follow> followedList = new ArrayList<>();
        for (long i = 0; i < 5; i++) {
            Member followingMember = Member.builder()
                    .id(i)
                    .nickname("member" + i)
                    .description("description" + i)
                    .build();

            int nextInt = new Random().nextInt(100);
            MemberProfileImage memberProfileImage = MemberProfileImage.builder()
                    .id(i)
                    .fileStorageId((long) nextInt)
                    .fileName("fileName" + nextInt)
                    .build();

            followingMember.setProfileImage(memberProfileImage);

            Follow follow = Follow.init(followingMember, followedMember);

            followingMembers.add(followingMember);
            followedList.add(follow);
        }

        CriteriaDto criteriaDto = new CriteriaDto();

        given(memberService.getMember(followedMember.getId())).willReturn(followedMember);
        given(followService.getFollowsByFollowedMemberId(followedMember.getId(), criteriaDto)).willReturn(followedList);

        //when
        List<FollowMemberResponseDto> followerMemberList = followApplicationService.getFollowerMemberList(followedMember.getId(), criteriaDto);

        //then
        then(memberService).should(times(1)).getMember(followedMember.getId());
        then(followService).should(times(1)).getFollowsByFollowedMemberId(followedMember.getId(), criteriaDto);

        followerMemberList.forEach(followMemberResponseDto -> {
            System.out.println("followMemberResponseDto = " + followMemberResponseDto);
            assertNotNull(followMemberResponseDto.getMemberId());
            assertNotNull(followMemberResponseDto.getNickname());
            assertNotNull(followMemberResponseDto.getDescription());
            assertNotNull(followMemberResponseDto.getProfileImageUrl());
            assertNotNull(followMemberResponseDto.getProfileImageThumbnailUrl());

            assertTrue(followingMembers.stream().anyMatch(member -> member.getId().equals(followMemberResponseDto.getMemberId())),
                    "조회된 회원 목록의 식별 ID 중 하나는 followedMember 를 팔로우하는 회원 중 하나의 식별 ID 와 일치해야 한다.");
        });

    }

    @Test
    @DisplayName("맞팔로우 회원 목록 조회")
    public void getTwoWayFollowMemberList() {
        //given
        Member followingMember = Member.builder()
                .id(1L)
                .build();


        List<Member> followedMembers = new ArrayList<>();
        List<Follow> twoWayFollows = new ArrayList<>();
        for (long i = 4; i < 4 + 5; i++) {
            Member followedMember = Member.builder()
                    .id(i)
                    .nickname("member" + i)
                    .description("description" + i)
                    .build();

            int nextInt = new Random().nextInt(100);
            MemberProfileImage memberProfileImage = MemberProfileImage.builder()
                    .id(i)
                    .fileStorageId((long) nextInt)
                    .fileName("fileName" + nextInt)
                    .build();

            followedMember.setProfileImage(memberProfileImage);

            followedMembers.add(followedMember);

            Follow following = Follow.builder()
                    .id(i + 100)
                    .followingMember(followingMember)
                    .followedMember(followedMember)
                    .status(FollowStatus.TWO_WAY_FOLLOW)
                    .build();

            twoWayFollows.add(following);
        }

        CriteriaDto criteriaDto = new CriteriaDto();

        given(memberService.getMember(followingMember.getId())).willReturn(followingMember);
        given(followService.getTwoWayFollowsByMemberId(followingMember.getId(), criteriaDto)).willReturn(twoWayFollows);

        //when
        List<FollowMemberResponseDto> twoWayFollowMemberList = followApplicationService.getTwoWayFollowMemberList(followingMember.getId(), criteriaDto);

        //then
        then(memberService).should(times(1)).getMember(followingMember.getId());
        then(followService).should(times(1)).getTwoWayFollowsByMemberId(followingMember.getId(), criteriaDto);

        twoWayFollowMemberList.forEach(followMemberResponseDto -> {
            System.out.println("followMemberResponseDto = " + followMemberResponseDto);
            assertNotNull(followMemberResponseDto.getMemberId());
            assertNotNull(followMemberResponseDto.getNickname());
            assertNotNull(followMemberResponseDto.getDescription());
            assertNotNull(followMemberResponseDto.getProfileImageUrl());
            assertNotNull(followMemberResponseDto.getProfileImageThumbnailUrl());

            assertTrue(followedMembers.stream().anyMatch(member -> member.getId().equals(followMemberResponseDto.getMemberId())),
                    "조회된 회원 목록의 식별 ID 중 하나는 followingMember 와 서로 팔로우하는 회원 중 하나의 식별 ID 와 일치해야 한다.");
        });


    }

}