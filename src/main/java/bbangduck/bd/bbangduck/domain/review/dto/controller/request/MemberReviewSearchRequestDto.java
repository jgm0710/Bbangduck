package bbangduck.bd.bbangduck.domain.review.dto.controller.request;

import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewSearchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO: 2021-06-15 주석
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberReviewSearchRequestDto {

    private ReviewSearchType searchType;

}
