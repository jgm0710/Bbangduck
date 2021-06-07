package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.controller.dto.DetailReviewResponseDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewResponseDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.SimpleReviewResponseDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewSearchDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * 작성자 : 정구민 <br><br>
 *
 * Review 응답 시 여러 Controller 에서 동시에 사용될 수 있는 부분에 대해
 * 코드 재사용율을 높이기 위해 구현한 utility class
 */
public class ReviewResponseUtils {
    public static ReviewResponseDto convertReviewToResponseDto(Review findReview, Member currentMember, boolean existsReviewLike) {
        switch (findReview.getReviewType()) {
            case SIMPLE:
                return SimpleReviewResponseDto.convert(findReview, currentMember, existsReviewLike);
            case DETAIL:
                return DetailReviewResponseDto.convert(findReview, currentMember, existsReviewLike);
            // TODO: 2021-06-07 수정
//            case DEEP:
//                return DeepReviewResponseDto.convert(findReview, currentMember, existsReviewLike);
            default:
                return null;
        }
    }

    public static ResponseStatus getCreateReviewResponseStatus(ReviewType reviewType) {
        switch (reviewType) {
            case SIMPLE:
                return ResponseStatus.CREATE_SIMPLE_REVIEW_SUCCESS;
            case DETAIL:
                return ResponseStatus.CREATE_DETAIL_REVIEW_SUCCESS;
            // TODO: 2021-06-07 수정 
//            case DEEP:
//                return ResponseStatus.CREATE_DEEP_REVIEW_SUCCESS;
            default:
                return null;
        }
    }

    public static String getThemeReviewListNextPageUrlString(Long themeId, ReviewSearchDto searchDto, long totalPagesCount) {
        int nextPageNum = searchDto.getNextPageNum();
        if (nextPageNum <= totalPagesCount) {
            return linkTo(methodOn(ThemeReviewApiController.class).getReviewList(themeId, null,null)).toUriComponentsBuilder()
                    .queryParam("pageNum", searchDto.getNextPageNum())
                    .queryParam("amount", searchDto.getAmount())
                    .queryParam("sortCondition", searchDto.getSortCondition()).toUriString();
        }
        return null;
    }

    public static String getThemeReviewListPrevPageUriString(Long themeId, ReviewSearchDto searchDto, long totalPagesCount) {
        Integer prevPageNum = searchDto.getPrevPageNum();
        if (prevPageNum != null && totalPagesCount >= prevPageNum) {
            return linkTo(methodOn(ThemeReviewApiController.class).getReviewList(themeId, null, null)).toUriComponentsBuilder()
                    .queryParam("pageNum", searchDto.getPrevPageNum())
                    .queryParam("amount", searchDto.getAmount())
                    .queryParam("sortCondition", searchDto.getSortCondition()).toUriString();
        }

        return null;
    }

    public static long calculateTotalPagesCount(long totalResultsCount, int amount) {
        long totalPagesCount = totalResultsCount / amount;
        if (totalResultsCount % amount != 0) {
            totalPagesCount++;
        }
        return totalPagesCount;
    }
}
