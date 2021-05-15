package bbangduck.bd.bbangduck.domain.member.entity;

import bbangduck.bd.bbangduck.domain.auth.service.dto.MemberSignUpDto;
import bbangduck.bd.bbangduck.domain.member.service.dto.MemberUpdateDto;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntityDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String email;

    private String password;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "member")
    private MemberProfileImage profileImage = null;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<SocialAccount> socialAccounts = new ArrayList<>();

    private String nickname;

    private String description;

    private int reviewCount;

    // TODO: 2021-05-13 방탈출 공개 여부 코드에 반영
    private boolean roomEscapeRecordVisible;

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
    public Member(String email, String password, String nickname, String description, int reviewCount,Set<MemberRole> roles, boolean roomEscapeRecordVisible, RefreshInfo refreshInfo) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.description = description;
        this.reviewCount = reviewCount;
        this.roomEscapeRecordVisible = roomEscapeRecordVisible;
        this.refreshInfo = refreshInfo;
        this.roles = roles;
    }

    public static Member signUp(MemberSignUpDto signUpServiceDto, int refreshTokenExpiredDate) {
        Member member = Member.builder()
                .email(signUpServiceDto.getEmail())
                .nickname(signUpServiceDto.getNickname())
                .password(signUpServiceDto.getPassword())
                .description(null)
                .reviewCount(0)
                .roomEscapeRecordVisible(true)
                .refreshInfo(RefreshInfo.init(refreshTokenExpiredDate))
                .roles(Set.of(MemberRole.USER))
                .build();

        String socialId = signUpServiceDto.getSocialId();
        SocialType socialType = signUpServiceDto.getSocialType();

        if (socialId != null && !socialId.isBlank() && socialType != null) {
            SocialAccount socialAccount = SocialAccount.builder()
                    .socialId(signUpServiceDto.getSocialId())
                    .socialType(signUpServiceDto.getSocialType())
                    .build();

            member.addSocialAccount(socialAccount);
        }

        return member;
    }

    public void setProfileImage(MemberProfileImage profileImage) {
        this.profileImage = profileImage;
        profileImage.setMember(this);
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
//                ", socialAccounts=" + socialAccounts +
                ", nickname='" + nickname + '\'' +
                ", description='" + description + '\'' +
                ", reviewCount=" + reviewCount +
                ", roomEscapeRecordVisible=" + roomEscapeRecordVisible +
                ", refreshInfo=" + refreshInfo +
                ", roles=" + roles +
                ", registerDate=" + registerDate +
                ", updateDate=" + updateDate +
                '}';
    }

    public boolean isRoomEscapeRecordVisible() {
        return roomEscapeRecordVisible;
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

    public void withdrawal() {
        this.roles = Set.of(MemberRole.WITHDRAWAL);
    }

    public void refresh(int refreshTokenExpiredDate) {
        this.refreshInfo = RefreshInfo.init(refreshTokenExpiredDate);
    }

    public void updateProfile(MemberUpdateDto modifyDto) {
        this.nickname = modifyDto.getNickname();
        this.description = modifyDto.getDescription();
        MemberProfileImage newProfileImage = MemberProfileImage.create(modifyDto.getProfileImageDto());
        setProfileImage(newProfileImage);
    }
}
