package bbangduck.bd.bbangduck.domain.review.service.dto;

import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 생성에 대한 Service 로직 구현 시 필요한 데이터들을 Dto 단위로 이동시키기 위해 구현한 Service Dto
 * Controller 단과의 의존 관계를 최소화 하기 위해 구현
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewCreateDto {

    /**
     * 간단 리뷰
     */

    private ReviewType reviewType;

    private boolean clearYN;

    private LocalTime clearTime;

    private Integer hintUsageCount;

    private Integer rating;

    private List<Long> friendIds;

    /**
     * 상세 리뷰
     */

    private List<ReviewImageDto> reviewImages;

    private String comment;

    @Builder
    public ReviewCreateDto(ReviewType reviewType, boolean clearYN, LocalTime clearTime, Integer hintUsageCount, Integer rating, List<Long> friendIds, List<ReviewImageDto> reviewImages, String comment) {
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

    public LocalTime getClearTime() {
        return clearTime;
    }

    public Integer getHintUsageCount() {
        return hintUsageCount;
    }

    public Integer getRating() {
        return rating;
    }

    public List<ReviewImageDto> getReviewImages() {
        return reviewImages;
    }

    public String getComment() {
        return comment;
    }

    public List<Long> getFriendIds() {
        return friendIds;
    }

    public boolean reviewImagesExists() {
        return reviewImages != null && !reviewImages.isEmpty();
    }

    public boolean isClearYN() {
        return clearYN;
    }
}
