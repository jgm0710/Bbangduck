package bbangduck.bd.bbangduck.domain.member.entity;

import bbangduck.bd.bbangduck.domain.member.dto.service.MemberProfileImageDto;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(unique = true)
    private Long fileStorageId;

    @Column(length = 1000)
    private String fileName;

    @Builder
    protected MemberProfileImage(Long id, Member member, Long fileStorageId, String fileName) {
        this.id = id;
        this.member = member;
        this.fileStorageId = fileStorageId;
        this.fileName = fileName;
    }

    public static MemberProfileImage create(MemberProfileImageDto memberProfileImageDto) {
        return MemberProfileImage.builder()
                .member(null)
                .fileStorageId(memberProfileImageDto.getFileStorageId())
                .fileName(memberProfileImageDto.getFileName())
                .build();
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Long getFileStorageId() {
        return fileStorageId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    @Override
    public String toString() {
        return "MemberProfileImage{" +
                "id=" + id +
//                ", member=" + member +
                ", fileId=" + fileStorageId +
                ", fileName='" + fileName + '\'' +
                '}';
    }

    public void update(MemberProfileImageDto memberProfileImageDto) {
        this.fileStorageId = memberProfileImageDto.getFileStorageId();
        this.fileName = memberProfileImageDto.getFileName();
    }

}
