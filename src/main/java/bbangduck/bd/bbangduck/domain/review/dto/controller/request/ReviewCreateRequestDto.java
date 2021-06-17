package bbangduck.bd.bbangduck.domain.review.dto.controller.request;

import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewHintUsageCount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 생성 시 클라이언트로부터 리뷰 생성에 필요한 데이터를 받을 때 사용할 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateRequestDto {

    @NotNull(message = "게임 클리어 여부를 기입해주세요.")
    private Boolean clearYN;

    private LocalTime clearTime;

    @NotNull(message = "사용한 힌트 개수를 기입해 주세요.")
    private ReviewHintUsageCount hintUsageCount;

    @NotNull(message = "테마에 대한 평점을 기입해 주세요.")
    @Range(min = 1, max = 5, message = "테마에 대한 평점은 1~5점만 기입이 가능합니다.")
    private Integer rating;

    private List<Long> friendIds;

    public ReviewCreateDto toServiceDto() {
        return ReviewCreateDto.builder()
                .clearYN(clearYN)
                .clearTime(clearTime)
                .hintUsageCount(hintUsageCount)
                .rating(rating)
                .friendIds(friendIds)
                .build();
    }

}
