package bbangduck.bd.bbangduck.domain.member.entity;

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
public class MemberProfileImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_profile_image_id")
    private Long id;

    private String fileName;

    private String fileStoragePath;

    private String fileDownloadUrl;

    private String fileThumbnailDownloadUrl;

    private String fileType;

    private String fileSize;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

}
