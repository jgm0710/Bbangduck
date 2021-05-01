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
                ", simpleIntroduction='" + simpleIntroduction + '\'' +
                ", reviewCount=" + reviewCount +
//                ", refreshInfo=" + refreshInfo +
                ", roles=" + roles +
                '}';
    }

    public static Member signUp(MemberSignUpDto signUpDto, JwtSecurityProperties jwtSecurityProperties) {
        Member member = Member.builder()
                .email(signUpDto.getEmail())
                .password(signUpDto.getPassword())
                .profileImage(null)
                .nickname(signUpDto.getNickname())
                .simpleIntroduction(null)
                .reviewCount(0)
                .refreshInfo(RefreshInfo.init(jwtSecurityProperties))
                .roles(Set.of(MemberRole.USER))
                .build();

        SocialAccount socialAccount = SocialAccount.builder()
                .socialId(signUpDto.getSocialId())
                .socialType(signUpDto.getSocialType())
                .member(member)
                .build();

        member.socialAccountList.add(socialAccount);

        return member;
    }

    public List<String> getRoleNameList() {
        return this.roles.stream().map(MemberRole::getRoleName).collect(Collectors.toList());
    }

}
