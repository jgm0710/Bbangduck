package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.controller.dto.*;
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
            case DEEP:
                return DeepReviewResponseDto.convert(findReview, currentMember, existsReviewLike);
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
            case DEEP:
                return ResponseStatus.CREATE_DEEP_REVIEW_SUCCESS;
            default:
                return null;
        }
    }

    public static String getThemeReviewListNextPageUrlString(Long themeId, ReviewSearchDto searchDto, Member currentMember, long totalPageCount) {
        int nextPageNum = searchDto.getNextPageNum();
        if (nextPageNum <= totalPageCount) {
            ThemeReviewSearchRequestDto nextPageSearchRequestDto = ThemeReviewSearchRequestDto.builder()
                    .pageNum(nextPageNum)
                    .amount(searchDto.getAmount())
                    .sortCondition(searchDto.getSortCondition())
                    .build();

            return linkTo(methodOn(ThemeReviewApiController.class).getReviewList(themeId, nextPageSearchRequestDto, currentMember)).toUri().toString();
        }
        return null;
    }

    public static String getThemeReviewListPrevPageUriString(Long themeId, ReviewSearchDto searchDto, Member currentMember) {
        Integer prevPageNum = searchDto.getPrevPageNum();
        if (prevPageNum != null) {
            ThemeReviewSearchRequestDto prevPageSearchRequestDto = ThemeReviewSearchRequestDto.builder()
                    .pageNum(prevPageNum)
                    .amount(searchDto.getAmount())
                    .sortCondition(searchDto.getSortCondition())
                    .build();

            return linkTo(methodOn(ThemeReviewApiController.class).getReviewList(themeId, prevPageSearchRequestDto, currentMember)).toUri().toString();
        }
        return null;
    }
}
