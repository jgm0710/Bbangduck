package bbangduck.bd.bbangduck.domain.review.dto.service;

import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewHintUsageCount;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

/**
 * 작성자 : JGM <br>
 * 작성 일자 : 2021-06-12 <br><br>
 *
 * 리뷰 수정 요청 시 필요한 Data 를 담을 Service Dto
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewUpdateDto {

    /**
     * 간단 리뷰
     */

    private ReviewType reviewType;

    private boolean clearYN;

    private LocalTime clearTime;

    private ReviewHintUsageCount hintUsageCount;

    private Integer rating;

    private List<Long> friendIds;

    /**
     * 상세 리뷰
     */

    private List<ReviewImageDto> reviewImages;

    private String comment;

    @Builder
    public ReviewUpdateDto(ReviewType reviewType, boolean clearYN, LocalTime clearTime, ReviewHintUsageCount hintUsageCount, Integer rating, List<Long> friendIds, List<ReviewImageDto> reviewImages, String comment) {
        this.reviewType = reviewType;
        this.clearYN = clearYN;
        this.clearTime = clearTime;
        this.hintUsageCount = hintUsageCount;
        this.rating = rating;
        this.friendIds = friendIds;
        this.reviewImages = reviewImages;
        this.comment = comment;
    }

    public ReviewType getReviewType() {
        return reviewType;
    }

    public boolean isClearYN() {
        return clearYN;
    }

    public LocalTime getClearTime() {
        return clearTime;
    }

    public ReviewHintUsageCount getHintUsageCount() {
        return hintUsageCount;
    }

    public Integer getRating() {
        return rating;
    }

    public List<Long> getFriendIds() {
        return friendIds;
    }

    public List<ReviewImageDto> getReviewImages() {
        return reviewImages;
    }

    public String getComment() {
        return comment;
    }
}
