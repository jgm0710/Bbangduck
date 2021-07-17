package bbangduck.bd.bbangduck.domain.review.dto.controller.request;

import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewDetailCreateDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewImageDto;
import bbangduck.bd.bbangduck.global.common.NullCheckUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 작성자 : 정구민 <br>
 * 작성 일자 : 2021-06-13 <br><br>
 *
 * 리뷰에 리뷰 상세 등록 요청 시 요청 Body 의 Data 를 담을 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDetailCreateRequestDto {

    private List<ReviewImageRequestDto> reviewImages;

    @NotBlank(message = "상세 리뷰에 등록할 코멘트를 기입해 주세요.")
    @Length(max = 2000, message = "코멘트는 2000자를 넘길 수 없습니다.")
    private String comment;

    public ReviewDetailCreateDto toServiceDto() {
        return ReviewDetailCreateDto.builder()
                .reviewImageDtos(convertReviewImages())
                .comment(comment)
                .build();
    }

    private List<ReviewImageDto> convertReviewImages() {
        if (NullCheckUtils.existsList(this.reviewImages)) {
            return reviewImages.stream().map(ReviewImageRequestDto::toServiceDto).collect(Collectors.toList());
        }
        return null;
    }

}
