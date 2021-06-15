package bbangduck.bd.bbangduck.domain.review.dto.service;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 작성자 : 정구민 <br>
 * 작성 일자 : 2021-06-13 <br><br>
 *
 * 리뷰 수정 시 필요한 Data 를 담을 Service Dto
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewDetailUpdateDto {

    private List<ReviewImageDto> reviewImageDtos;

    private String comment;

    @Builder
    public ReviewDetailUpdateDto(List<ReviewImageDto> reviewImageDtos, String comment) {
        this.reviewImageDtos = reviewImageDtos;
        this.comment = comment;
    }

    public List<ReviewImageDto> getReviewImageDtos() {
        return reviewImageDtos;
    }

    public String getComment() {
        return comment;
    }

}
