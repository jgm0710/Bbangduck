package bbangduck.bd.bbangduck.domain.review.entity;

import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewImageDto;
import lombok.AccessLevel;
import lombok.Builder;
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
    private ReviewDetail reviewDetail;

    private Long fileStorageId;

    private String fileName;

    @Builder
    public ReviewImage(Long id, ReviewDetail reviewDetail, Long fileStorageId, String fileName) {
        this.id = id;
        this.reviewDetail = reviewDetail;
        this.fileStorageId = fileStorageId;
        this.fileName = fileName;
    }

    public static ReviewImage create(ReviewImageDto reviewImageDto) {
        return ReviewImage.builder()
                .fileStorageId(reviewImageDto.getFileStorageId())
                .fileName(reviewImageDto.getFileName())
                .build();
    }

    public void setReviewDetail(ReviewDetail reviewDetail) {
        this.reviewDetail = reviewDetail;
    }

    public Long getId() {
        return id;
    }

    public ReviewDetail getReviewDetail() {
        return reviewDetail;
    }

    public Long getFileStorageId() {
        return fileStorageId;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return "ReviewImage{" +
                "id=" + id +
//                ", review=" + review +
                ", fileStorageId=" + fileStorageId +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
