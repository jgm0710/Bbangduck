package bbangduck.bd.bbangduck.domain.review.dto.service;

import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewHintUsageCount;
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

    private boolean clearYN;

    private LocalTime clearTime;

    private ReviewHintUsageCount hintUsageCount;

    private Integer rating;

    private List<Long> friendIds;

    @Builder
    public ReviewCreateDto(boolean clearYN, LocalTime clearTime, ReviewHintUsageCount hintUsageCount, Integer rating, List<Long> friendIds) {
        this.clearYN = clearYN;
        this.clearTime = clearTime;
        this.hintUsageCount = hintUsageCount;
        this.rating = rating;
        this.friendIds = friendIds;
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

    public boolean isClearYN() {
        return clearYN;
    }
}
