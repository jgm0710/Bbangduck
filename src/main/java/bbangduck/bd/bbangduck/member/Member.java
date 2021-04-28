package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.common.BaseEntityDateTime;
import bbangduck.bd.bbangduck.member.social.SocialAccount;
import lombok.*;
import lombok.Builder.Default;

import javax.jdo.annotations.Join;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntityDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String email;

    private String password;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "member")
    private MemberProfileImage profileImage;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Default
    private List<SocialAccount> socialAccountList = new ArrayList<>();

    private String nickname;

    private String simpleIntroduction;

    private int reviewCount;

    @Embedded
    private RefreshInfo refreshInfo;

    @Default
    @ElementCollection(targetClass = MemberRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "member_role", joinColumns = @JoinColumn(name = "member_id"))
    @Enumerated(EnumType.STRING)
    private Set<MemberRole> roles = new HashSet<>();

}
