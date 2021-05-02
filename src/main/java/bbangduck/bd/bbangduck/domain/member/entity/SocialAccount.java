package bbangduck.bd.bbangduck.domain.member.entity;

import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원에 등록된 Social 인증 정보를 담을 Entity
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    public void setMember(Member member) {
        this.member = member;
    }
}
