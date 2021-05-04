package bbangduck.bd.bbangduck.domain.member.entity;

import lombok.*;

import javax.persistence.*;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 프로필 이미지 Entity
 * Database 의 회원 프로필 이미지 테이블과 연결
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProfileImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_profile_image_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private String fileDownloadUrl;

    private String fileThumbnailDownloadUrl;


    @Builder
    protected MemberProfileImage(Long id, Member member, String fileDownloadUrl, String fileThumbnailDownloadUrl) {
        this.id = id;
        this.member = member;
        this.fileDownloadUrl = fileDownloadUrl;
        this.fileThumbnailDownloadUrl = fileThumbnailDownloadUrl;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public String getFileDownloadUrl() {
        return fileDownloadUrl;
    }

    public String getFileThumbnailDownloadUrl() {
        return fileThumbnailDownloadUrl;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
