package bbangduck.bd.bbangduck.domain.member.entity;

import bbangduck.bd.bbangduck.domain.auth.dto.service.MemberSignUpDto;
import bbangduck.bd.bbangduck.domain.member.entity.enbeded.RefreshInfo;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRoomEscapeRecodesOpenStatus;
import bbangduck.bd.bbangduck.domain.member.enumerate.SocialType;
import bbangduck.bd.bbangduck.domain.member.repository.MemberProfileImageRepository;
import bbangduck.bd.bbangduck.domain.member.dto.service.MemberProfileImageDto;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 작성자 : 정구민 <br><br>
 * <p>
 * 회원 Entity <br>
 * Database 의 회원 테이블과 연결
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Member extends BaseEntityDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String email;

    private String password;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "member", fetch = FetchType.EAGER)
    private MemberProfileImage profileImage = null;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<SocialAccount> socialAccounts = new ArrayList<>();

    private String nickname;

    private String description;

    @Enumerated(EnumType.STRING)
    private MemberRoomEscapeRecodesOpenStatus roomEscapeRecodesOpenStatus;

    @Embedded
    private RefreshInfo refreshInfo;

    @ElementCollection(targetClass = MemberRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "member_role", joinColumns = @JoinColumn(name = "member_id"))
    @Enumerated(EnumType.STRING)
    private Set<MemberRole> roles;

    @Builder
    public Member(Long id, String email, String password, String nickname, String description, Set<MemberRole> roles, MemberRoomEscapeRecodesOpenStatus roomEscapeRecodesOpenStatus, RefreshInfo refreshInfo) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.description = description;
        this.roomEscapeRecodesOpenStatus = roomEscapeRecodesOpenStatus;
        this.refreshInfo = refreshInfo;
        this.roles = roles;
    }

    public static Member signUp(MemberSignUpDto signUpServiceDto, int refreshTokenExpiredDate) {
        Member member = Member.builder()
                .email(signUpServiceDto.getEmail())
                .nickname(signUpServiceDto.getNickname())
                .password(signUpServiceDto.getPassword())
                .description(null)
                .roomEscapeRecodesOpenStatus(MemberRoomEscapeRecodesOpenStatus.OPEN)
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

    public String getRefreshToken() {
        return refreshInfo == null ? null : refreshInfo.getRefreshToken();
    }

    public LocalDateTime getRefreshTokenExpiredDate() {
        return refreshInfo == null ? null : refreshInfo.getRefreshTokenExpiredDate();
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
                ", roomEscapeRecordsOpenYN=" + roomEscapeRecodesOpenStatus +
                ", refreshInfo=" + refreshInfo +
                ", roles=" + roles +
                ", registerTimes=" + registerTimes +
                ", updateDate=" + updateTimes +
                '}';
    }

    public MemberRoomEscapeRecodesOpenStatus getRoomEscapeRecodesOpenStatus() {
        return roomEscapeRecodesOpenStatus;
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
        this.refreshInfo = null;
    }

    public void signIn(int refreshTokenExpiredDate) {
        this.refreshInfo = RefreshInfo.init(refreshTokenExpiredDate);
    }

    public void updateProfileImage(MemberProfileImageDto memberProfileImageDto) {
        this.profileImage.update(memberProfileImageDto);
    }

    public void createProfileImage(MemberProfileImageDto memberProfileImageDto) {
        MemberProfileImage newProfileImage = MemberProfileImage.create(memberProfileImageDto);
        setProfileImage(newProfileImage);
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateRoomEscapeRecodesOpenStatus(MemberRoomEscapeRecodesOpenStatus memberRoomEscapeRecodesOpenStatus) {
        this.roomEscapeRecodesOpenStatus = memberRoomEscapeRecodesOpenStatus;
    }

    public void deleteProfileImage(MemberProfileImageRepository memberProfileImageRepository) {
        memberProfileImageRepository.delete(profileImage);
        profileImage = null;
    }

    public void signOut() {
        this.refreshInfo = null;
    }

    public String getProfileImageFileName() {
        return profileImage == null ? null : profileImage.getFileName();
    }

    public boolean isMyId(Long memberId) {
        return this.id.equals(memberId);
    }
}
