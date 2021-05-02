package bbangduck.bd.bbangduck.domain.member.entity;

import bbangduck.bd.bbangduck.domain.member.dto.MemberSignUpDto;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import bbangduck.bd.bbangduck.global.config.properties.JwtSecurityProperties;
import lombok.*;
import lombok.Builder.Default;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 Entity <br>
 * Database 의 회원 테이블과 연결
 */
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
    private List<SocialAccount> socialAccounts = new ArrayList<>();

    private String nickname;

    private String description;

    private int reviewCount;

    @Embedded
    private RefreshInfo refreshInfo;

    @Default
    @ElementCollection(targetClass = MemberRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "member_role", joinColumns = @JoinColumn(name = "member_id"))
    @Enumerated(EnumType.STRING)
    private Set<MemberRole> roles = new HashSet<>();


//.id()
//.email()
//.password()
//.profileImage()
//.socialAccountList()
//.nickname()
//.simpleIntroduction()
//.reviewCount()
//.refreshInfo()
//.roles()


    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
//                ", profileImage=" + profileImage +
//                ", socialAccountList=" + socialAccountList +
                ", nickname='" + nickname + '\'' +
                ", simpleIntroduction='" + description + '\'' +
                ", reviewCount=" + reviewCount +
//                ", refreshInfo=" + refreshInfo +
                ", roles=" + roles +
                '}';
    }

    public List<String> getRoleNameList() {
        return this.roles.stream().map(MemberRole::getRoleName).collect(Collectors.toList());
    }

    public void addSocialAccount(SocialAccount socialAccount) {
        this.socialAccounts.add(socialAccount);
        socialAccount.setMember(this);
    }
}
