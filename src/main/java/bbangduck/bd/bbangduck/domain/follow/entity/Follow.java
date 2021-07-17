package bbangduck.bd.bbangduck.domain.follow.entity;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow extends BaseEntityDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_friend_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member followingMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id")
    private Member followedMember;

    @Enumerated(EnumType.STRING)
    private FollowStatus status;

    @Builder
    public Follow(Long id, Member followingMember, Member followedMember, FollowStatus status) {
        this.id = id;
        this.followingMember = followingMember;
        this.followedMember = followedMember;
        this.status = status;
    }

    public static Follow init(Member followingMember, Member followedMember) {
        return Follow.builder()
                .followingMember(followingMember)
                .followedMember(followedMember)
                .status(FollowStatus.ONE_WAY_FOLLOW)
                .build();
    }

    public Long getId() {
        return id;
    }

    public Member getFollowingMember() {
        return followingMember;
    }

    public Member getFollowedMember() {
        return followedMember;
    }

    public void updateStatus(FollowStatus status) {
        this.status = status;
    }

    public FollowStatus getStatus() {
        return status;
    }
}
