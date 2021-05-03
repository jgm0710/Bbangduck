package bbangduck.bd.bbangduck.domain.member.entity;

import bbangduck.bd.bbangduck.domain.member.dto.MemberSignUpDto;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import bbangduck.bd.bbangduck.global.config.properties.JwtSecurityProperties;
import lombok.*;
import lombok.Builder.Default;

import javax.persistence.*;
import java.time.LocalDateTime;
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
// FIXME: 2021-05-02 Getter, Builder 를 롬복을 사용하지 않고 구현
@Entity
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
    private List<SocialAccount> socialAccounts = new ArrayList<>();

    private String nickname;

    private String description;

    private int reviewCount;

    @Embedded
    private RefreshInfo refreshInfo;

    @ElementCollection(targetClass = MemberRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "member_role", joinColumns = @JoinColumn(name = "member_id"))
    @Enumerated(EnumType.STRING)
    private Set<MemberRole> roles;


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


    @Builder
    public Member(String email, String password, MemberProfileImage profileImage, String nickname, String description, int reviewCount,Set<MemberRole> roles, RefreshInfo refreshInfo) {
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.description = description;
        this.reviewCount = reviewCount;
        this.refreshInfo = refreshInfo;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public MemberProfileImage getProfileImage() {
        return profileImage;
    }

    public List<SocialAccount> getSocialAccounts() {
        return socialAccounts;
    }

    public String getNickname() {
        return nickname;
    }

    public String getDescription() {
        return description;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public String getRefreshToken() {
        return refreshInfo.getRefreshToken();
    }

    public LocalDateTime getRefreshTokenExpiredDate() {
        return refreshInfo.getRefreshTokenExpiredDate();
    }

    public Set<MemberRole> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
//                ", profileImage=" + profileImage +
//                ", socialAccountList=" + socialAccountList +
                ", nickname='" + nickname + '\'' +
                ", description='" + description + '\'' +
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

    public SocialAccount getFirstSocialAccount() {
        return socialAccounts.stream().findFirst().orElse(null);
    }
}
