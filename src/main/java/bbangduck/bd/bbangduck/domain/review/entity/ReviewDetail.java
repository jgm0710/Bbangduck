package bbangduck.bd.bbangduck.domain.review.entity;

import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewDetailCreateDto;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewDetailUpdateDto;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewImageDto;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static bbangduck.bd.bbangduck.global.common.NullCheckUtils.existsList;

/**
 * 작성자 : 정구민 <br>
 * 작성 일자 : 2021-06-13 <br><br>
 *
 *  리뷰에 이미지, 코멘트 등 상세적인 정보를 입력할 경우 값을 저장할 Entity
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewDetail extends BaseEntityDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_detail_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @OneToMany(mappedBy = "reviewDetail", cascade = CascadeType.ALL)
    private List<ReviewImage> reviewImages = new ArrayList<>();

    @Column(length = 3000)
    private String comment;

    @Builder
    public ReviewDetail(Long id, Review review, String comment) {
        this.id = id;
        this.review = review;
        this.comment = comment;
    }

    public static ReviewDetail create(ReviewDetailCreateDto reviewDetailCreateDto) {
        ReviewDetail reviewDetail = ReviewDetail.builder()
                .comment(reviewDetailCreateDto.getComment())
                .build();

        List<ReviewImageDto> reviewImageDtos = reviewDetailCreateDto.getReviewImageDtos();
        if (existsList(reviewImageDtos)) {
            reviewImageDtos.forEach(reviewImageDto -> {
                ReviewImage reviewImage = ReviewImage.create(reviewImageDto);
                reviewDetail.addReviewImage(reviewImage);
            });
        }

        return reviewDetail;
    }

    public Long getId() {
        return id;
    }

    public Review getReview() {
        return review;
    }

    public List<ReviewImage> getReviewImages() {
        return reviewImages;
    }

    public String getComment() {
        return comment;
    }

    public void addReviewImage(ReviewImage reviewImage) {
        this.reviewImages.add(reviewImage);
        reviewImage.setReviewDetail(this);
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public void update(ReviewDetailUpdateDto reviewDetailUpdateDto) {
        this.comment = reviewDetailUpdateDto.getComment();

        List<ReviewImageDto> reviewImageDtos = reviewDetailUpdateDto.getReviewImageDtos();
        if (existsList(reviewImageDtos)) {
            reviewImageDtos.forEach(reviewImageDto -> {
                ReviewImage reviewImage = ReviewImage.create(reviewImageDto);
                addReviewImage(reviewImage);
            });
        }
    }
}
