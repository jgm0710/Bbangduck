package bbangduck.bd.bbangduck.domain.friend.entity;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberFriend extends BaseEntityDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_friend_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id")
    private Member friend;

    @Enumerated(EnumType.STRING)
    private MemberFriendStatus status;

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Member getFriend() {
        return friend;
    }

    public MemberFriendStatus getStatus() {
        return status;
    }
}
