package bbangduck.bd.bbangduck.domain.review.controller.dto;

import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewDetailUpdateDto;
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
 * 리뷰에 등록된 리뷰 상세 수정 요청 Body 의 Data 를 담을 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDetailUpdateRequestDto {

    private List<ReviewImageRequestDto> reviewImages;

    @NotBlank(message = "상세 리뷰에 등록할 코멘트를 기입해 주세요.")
    @Length(max = 2000, message = "코멘트는 2000자를 넘길 수 없습니다.")
    private String comment;

    public ReviewDetailUpdateDto toServiceDto() {
        return ReviewDetailUpdateDto.builder()
                .reviewImageDtos(reviewImages.stream().map(ReviewImageRequestDto::toServiceDto).collect(Collectors.toList()))
                .comment(comment)
                .build();
    }

}
