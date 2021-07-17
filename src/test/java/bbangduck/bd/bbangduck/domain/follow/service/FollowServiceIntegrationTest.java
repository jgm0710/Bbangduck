package bbangduck.bd.bbangduck.domain.follow.service;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.follow.entity.Follow;
import bbangduck.bd.bbangduck.domain.follow.repository.FollowRepository;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FollowService 통합 테스트")
class FollowServiceIntegrationTest extends BaseTest {

    @Autowired
    FollowService followService;

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
    @DisplayName("팔로우 - 기존에 관계가 없던 회원인 경우")
    public void follow() {
        //given
        Member followingMember = Member.builder().build();
        Member followedMember = Member.builder().build();

        memberRepository.save(followingMember);
        memberRepository.save(followedMember);

        //when
        followService.follow(followingMember, followedMember);

        //then
        boolean present = followRepository.findByFollowingMemberAndFollowedMember(followingMember, followedMember).isPresent();
        assertTrue(present, "팔로우가 있어야 한다.");
    }

    @Test
    @DisplayName("팔로우 - 기존에 팔로우 했던 회원인 경우")
    public void follow_AlreadyFollow() {
        //given
        Member followingMember = Member.builder().build();
        Member followedMember = Member.builder().build();

        memberRepository.save(followingMember);
        memberRepository.save(followedMember);

        Follow follow = Follow.builder()
                .followingMember(followingMember)
                .followedMember(followedMember)
                .build();
        Follow savedFollow = followRepository.save(follow);

        //when
        followService.follow(followingMember, followedMember);

        //then
        Follow findFollow = followRepository.findByFollowingMemberAndFollowedMember(followingMember, followedMember).orElseThrow(EntityNotFoundException::new);
        assertEquals(savedFollow.getId(), findFollow.getId(), "기존에 팔로우를 했던 경우 새로 팔로우 했을 때 followId 가 바뀌지 않아야 한다.");
    }

    @Test
    @DisplayName("두 회원이 양방향 팔로우 관계인지 확인")
    public void isTwoWayFollowMember() {
        //given
        Member member1 = Member.builder().build();
        Member member2 = Member.builder().build();

        memberRepository.save(member1);
        memberRepository.save(member2);

        Follow follow1 = Follow.builder()
                .followingMember(member1)
                .followedMember(member2)
                .build();
        Follow follow2 = Follow.builder()
                .followingMember(member2)
                .followedMember(member1)
                .build();

        followRepository.save(follow1);
        followRepository.save(follow2);


        //when
        boolean twoWayFollowRelation = followService.isTwoWayFollowRelation(member1.getId(), member2.getId());

        //then
        assertTrue(twoWayFollowRelation, "두 회원은 서로 양방향 팔로우 관계이다.");


    }

    @Test
    @DisplayName("두 회원이 양방향 팔로우 관계인지 확인 - 단방향 팔로우인 경우")
    public void isTwoWayFollowMember_OneWayFollow() {
        //given
        Member member1 = Member.builder().build();
        Member member2 = Member.builder().build();

        memberRepository.save(member1);
        memberRepository.save(member2);

        Follow follow1 = Follow.builder()
                .followingMember(member1)
                .followedMember(member2)
                .build();

        followRepository.save(follow1);

        //when
        boolean twoWayFollowRelation = followService.isTwoWayFollowRelation(member1.getId(), member2.getId());

        //then
        assertFalse(twoWayFollowRelation, "두 회원은 양방향 팔로우 관계가 아니어야 한다.");

    }

    @Test
    @DisplayName("한 회원과 여러명으 회원이 모두 양방향 팔로우 관계인지 확인")
    public void isTwoWayFollowMembers() {
        //given
        Member member1 = Member.builder().build();
        memberRepository.save(member1);

        List<Member> members = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Member member = Member.builder().build();
            memberRepository.save(member);
            members.add(member);

            Follow follow1 = Follow.builder()
                    .followingMember(member1)
                    .followedMember(member)
                    .build();

            Follow follow2 = Follow.builder()
                    .followingMember(member)
                    .followedMember(member1)
                    .build();

            followRepository.save(follow1);
            followRepository.save(follow2);
        }

        List<Long> memberIds = members.stream().map(Member::getId).collect(Collectors.toList());

        //when
        boolean twoWayFollowRelationMembers = followService.isTwoWayFollowRelationMembers(member1.getId(), memberIds);

        //then
        assertTrue(twoWayFollowRelationMembers, "member1 과 members 의 모든 회원은 서로 양방햔 팔로우 관계여야 한다.");

        memberIds.forEach(member2Id -> {
            boolean twoWayFollowRelation = followService.isTwoWayFollowRelation(member1.getId(), member2Id);
            assertTrue(twoWayFollowRelation, "모든 회원은 개별적으로 검증해도 서로 양방햔 팔로우 관계여야 한다.");
        });

    }

    @Test
    @DisplayName("한 회원과 여러명으 회원이 모두 양방향 팔로우 관계인지 확인 - 회원 중 한명이라도 단방향 팔로우 관계인 경우")
    public void isTwoWayFollowMembers_OneWayFollow() {
        //given
        Member member1 = Member.builder().build();
        memberRepository.save(member1);

        List<Member> members = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Member member = Member.builder().build();
            memberRepository.save(member);
            members.add(member);

            Follow follow1 = Follow.builder()
                    .followingMember(member1)
                    .followedMember(member)
                    .build();

            Follow follow2 = Follow.builder()
                    .followingMember(member)
                    .followedMember(member1)
                    .build();

            followRepository.save(follow1);
            followRepository.save(follow2);
        }

        Member otherMember = Member.builder().build();
        memberRepository.save(otherMember);
        Follow otherFollow = Follow.builder()
                .followingMember(member1)
                .followedMember(otherMember)
                .build();
        followRepository.save(otherFollow);

        members.add(otherMember);

        List<Long> memberIds = members.stream().map(Member::getId).collect(Collectors.toList());

        //when
        boolean twoWayFollowRelationMembers = followService.isTwoWayFollowRelationMembers(member1.getId(), memberIds);

        //then
        assertFalse(twoWayFollowRelationMembers,"관계를 조회하는 회원 중 한명은 양방향 팔로우 관계가 아니다.");

        List<Boolean> twoWayFollowRelationships = memberIds.stream().map(memberId -> followService.isTwoWayFollowRelation(member1.getId(), memberId)).collect(Collectors.toList());

        assertTrue(twoWayFollowRelationships.contains(false), "개별적으로 조회할 경우에도 양방향 팔로우 관계가 아닌 회원이 존재해야한다.");
    }

    @Test
    @DisplayName("팔로우 해제")
    public void unfollow() {
        //given
        Member followingMember = Member.builder().build();
        Member followedMember = Member.builder().build();

        Follow follow = Follow.builder()
                .followingMember(followingMember)
                .followedMember(followedMember)
                .build();

        memberRepository.save(followingMember);
        memberRepository.save(followedMember);
        followRepository.save(follow);

        assertTrue(followRepository.findByFollowingMemberAndFollowedMember(followingMember, followedMember).isPresent());

        //when
        followService.unfollow(followingMember.getId(), followedMember.getId());

        //then
        assertTrue(followRepository.findByFollowingMemberAndFollowedMember(followingMember, followedMember).isEmpty(), "팔로우 해제 시 팔로우가 조회되지 않아야 한다.");
    }



}