package bbangduck.bd.bbangduck.domain.review.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰에 등록될 이미지 파일의 목록에 대한 정보를 담을 Entity
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    private Long fileStorageId;

    private String fileName;

}
