package bbangduck.bd.bbangduck.member.social;

import bbangduck.bd.bbangduck.common.BaseEntityDateTime;
import bbangduck.bd.bbangduck.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

}
