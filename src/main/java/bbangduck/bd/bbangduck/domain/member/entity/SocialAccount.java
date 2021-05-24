package bbangduck.bd.bbangduck.domain.member.entity;

import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.*;

import javax.persistence.*;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원에 등록된 Social 인증 정보를 담을 Entity
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialAccount extends BaseEntityDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_account_id")
    private Long id;

    private String socialId;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    protected SocialAccount(Long id, String socialId, SocialType socialType, Member member) {
        this.id = id;
        this.socialId = socialId;
        this.socialType = socialType;
        this.member = member;
    }

    public Long getId() {
        return id;
    }

    public String getSocialId() {
        return socialId;
    }

    public SocialType getSocialType() {
        return socialType;
    }


    public void setMember(Member member) {
        this.member = member;
    }

    @Override
    public String toString() {
        return "SocialAccount{" +
                "id=" + id +
                ", socialId='" + socialId + '\'' +
                ", socialType=" + socialType +
//                ", member=" + member +
                '}';
    }
}
