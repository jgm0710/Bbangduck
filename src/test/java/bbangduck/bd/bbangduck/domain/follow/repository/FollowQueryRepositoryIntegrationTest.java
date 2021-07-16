package bbangduck.bd.bbangduck.domain.follow.repository;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.follow.entity.Follow;
import bbangduck.bd.bbangduck.domain.follow.entity.FollowStatus;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("FollowQueryRepository 통합 테스트")
class FollowQueryRepositoryIntegrationTest extends BaseTest {

    @Autowired
    FollowQueryRepository followQueryRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    FollowRepository followRepository;

    @AfterEach
    void tearDown() {
        followRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("특정 회원의 팔로잉 목록 조회")
    public void findListByFollowingMemberId() {
        //given
        Member followingMember1 = Member.builder().build();
        Member followingMember2 = Member.builder().build();

        memberRepository.save(followingMember1);
        memberRepository.save(followingMember2);

        List<Member> followedMembers1 = new ArrayList<>();
        List<Member> followedMembers2 = new ArrayList<>();


        for (int i = 0; i < 3; i++) {
            Member followedMember1 = Member.builder().build();
            memberRepository.save(followedMember1);
            followedMembers1.add(followedMember1);
            Follow follow1 = Follow.builder()
                    .followingMember(followingMember1)
                    .followedMember(followedMember1)
                    .build();
            followRepository.save(follow1);

            Member followedMember2 = Member.builder().build();
            memberRepository.save(followedMember2);
            followedMembers2.add(followedMember2);
            Follow follow2 = Follow.builder()
                    .followingMember(followingMember2)
                    .followedMember(followedMember2)
                    .build();
            followRepository.save(follow2);
        }

        //when
        System.out.println("==========================================================================================");
        List<Follow> findFollows1 = followQueryRepository.findListByFollowingMemberId(followingMember1.getId(), new CriteriaDto());
        System.out.println("==========================================================================================");

        //then
        assertEquals(3, findFollows1.size(), "조회된 목록은 3개이다.");
        findFollows1.forEach(follow -> {
            Member findFollowingMember = follow.getFollowingMember();
            Member findFollowedMember = follow.getFollowedMember();

            assertEquals(followingMember1.getId(), findFollowingMember.getId(), "조회된 팔로우 목록의 팔로윙 회원은 항상 followingMember1 이어야 한다.");

            boolean anyMatch = followedMembers1.stream().anyMatch(member -> member.getId().equals(findFollowedMember.getId()));
            assertTrue(anyMatch, "조회된 팔로우 목록의 팔로우된 회원 중 하나는 followingMember1 이 팔로우 한 회원 중 하나와 일치해야 한다.");
        });

    }

    @Test
    @DisplayName("팔로우 되는 회원 ID 를 통한 팔로우 목록 조회")
    public void findListByFollowedMemberId() {
        //given
        Member followedMember1 = Member.builder().build();
        Member followedMember2 = Member.builder().build();

        memberRepository.save(followedMember1);
        memberRepository.save(followedMember2);

        List<Member> followingMembers1 = new ArrayList<>();
        List<Member> followingMembers2 = new ArrayList<>();
        for (long i = 0; i < 3; i++) {
            Member followingMember1 = Member.builder().build();
            Follow follow1 = Follow.builder()
                    .followingMember(followingMember1)
                    .followedMember(followedMember1)
                    .build();

            memberRepository.save(followingMember1);
            followRepository.save(follow1);

            Member followingMember2 = Member.builder().build();
            Follow follow2 = Follow.builder()
                    .followingMember(followingMember2)
                    .followedMember(followedMember2)
                    .build();

            memberRepository.save(followingMember2);
            followRepository.save(follow2);

            followingMembers1.add(followingMember1);
            followingMembers2.add(followingMember2);
        }

        //when
        System.out.println("==========================================================================================");
        List<Follow> findFollows1 = followQueryRepository.findListByFollowedMemberId(followedMember1.getId(), new CriteriaDto());
        System.out.println("==========================================================================================");

        //then
        assertEquals(3, findFollows1.size(), "조회된 목록은 3개이다.");
        findFollows1.forEach(follow -> {
            Member findFollowingMember = follow.getFollowingMember();
            Member findFollowedMember = follow.getFollowedMember();

            assertEquals(followedMember1.getId(), findFollowedMember.getId(), "조회된 팔로우 목록의 팔로우된 회원은 항상 followedMember1 이어야 한다.");

            boolean anyMatch = followingMembers1.stream().anyMatch(member -> member.getId().equals(findFollowingMember.getId()));
            assertTrue(anyMatch, "조회된 팔로우 목록의 팔로우한 회원 중 하나는 followedMember1 을 팔로우 한 회원 중 하나와 일치해야 한다.");
        });

    }

    @Test
    @DisplayName("회원 ID 를 통한 맞팔로우 회원 목록 조회")
    public void findTwoWayFollowMembersByMemberId() {
        //given
        Member followingMember1 = Member.builder().build();
        Member followingMember2 = Member.builder().build();

        memberRepository.save(followingMember1);
        memberRepository.save(followingMember2);

        List<Member> followedMembers1 = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Member followedMember1 = Member.builder()
                    .nickname("followedMember1 + "+i)
                    .build();
            Member followedMember2 = Member.builder()
                    .nickname("followedMember2 + "+i)
                    .build();

            memberRepository.save(followedMember1);
            followedMembers1.add(followedMember1);
            memberRepository.save(followedMember2);

            Follow follow1 = Follow.builder()
                    .followingMember(followingMember1)
                    .followedMember(followedMember1)
                    .status(FollowStatus.TWO_WAY_FOLLOW)
                    .build();

            Follow follow2 = Follow.builder()
                    .followingMember(followedMember1)
                    .followedMember(followingMember1)
                    .status(FollowStatus.TWO_WAY_FOLLOW)
                    .build();

            Follow follow3 = Follow.builder()
                    .followingMember(followingMember2)
                    .followedMember(followedMember2)
                    .status(FollowStatus.TWO_WAY_FOLLOW)
                    .build();

            Follow follow4 = Follow.builder()
                    .followingMember(followedMember2)
                    .followedMember(followingMember2)
                    .status(FollowStatus.TWO_WAY_FOLLOW)
                    .build();

            followRepository.save(follow1);
            followRepository.save(follow2);
            followRepository.save(follow3);
            followRepository.save(follow4);
        }

        Member followedMember3 = Member.builder().build();
        memberRepository.save(followedMember3);

        Follow follow5 = Follow.init(followingMember1, followedMember3);
        Follow follow6 = Follow.init(followingMember2, followedMember3);

        followRepository.save(follow5);
        followRepository.save(follow6);

        //when
        System.out.println("==========================================================================================");
        List<Follow> twoWayFollows = followQueryRepository.findTwoWayFollowListByFollowingMemberId(followingMember1.getId(), new CriteriaDto());
        System.out.println("==========================================================================================");

        //then
        assertEquals(3, twoWayFollows.size(), "조회된 개수는 3개이다.");

        twoWayFollows.forEach(follow -> {
            Member findFollowingMember = follow.getFollowingMember();
            assertEquals(followingMember1.getId(), findFollowingMember.getId(), "조회된 팔로우의 팔로잉 회원은 모두 followingMember1 이다.");

            Member findFollowedMember = follow.getFollowedMember();
            boolean anyMatch = followedMembers1.stream().anyMatch(followedMember -> followedMember.getId().equals(findFollowedMember.getId()));
            assertTrue(anyMatch, "조회된 팔로우의 팔로우된 회원 중 하나는 followingMember1 이 팔로우한 회원 중 하나와 일치해야한다.");
        });
    }
}