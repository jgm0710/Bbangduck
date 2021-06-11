package bbangduck.bd.bbangduck.domain.review.controller.dto;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewImage;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 작성자 : 정구민 <br><br>
 * <p>
 * 리뷰 조회 시 해당 리뷰가 상세 리뷰일 경우 해당 Dto 를 통해서 Data 응답
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailReviewResponseDto extends SimpleReviewResponseDto {

    private List<ReviewImageResponseDto> reviewImages;

    private String comment;

    public static DetailReviewResponseDto convert(Review review, Member currentMember, boolean existReviewLike, long periodForAddingSurveys) {
        return new DetailReviewResponseDto(review, currentMember, existReviewLike, periodForAddingSurveys);
    }

    protected DetailReviewResponseDto(Review review, Member currentMember, boolean existReviewLike, long periodForAddingSurveys) {
        super(review, currentMember, existReviewLike, periodForAddingSurveys);

        this.reviewImages = convertReviewImages(review.getReviewImages());
        this.comment = review.getComment();
    }

    private List<ReviewImageResponseDto> convertReviewImages(List<ReviewImage> reviewImages) {
        if (reviewImagesExists(reviewImages)) {
            return reviewImages.stream().map(ReviewImageResponseDto::convert).collect(Collectors.toList());
        }
        return null;
    }

    private boolean reviewImagesExists(List<ReviewImage> reviewImages) {
        return reviewImages != null && !reviewImages.isEmpty();
    }
}
