package bbangduck.bd.bbangduck.domain.member.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 프로필 이미지 Entity
 * Database 의 회원 프로필 이미지 테이블과 연결
 */
// FIXME: 2021-05-02 Getter, Builder 를 롬복을 사용하지 않고 구현
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

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private String fileDownloadUrl;

    private String fileThumbnailDownloadUrl;

}
