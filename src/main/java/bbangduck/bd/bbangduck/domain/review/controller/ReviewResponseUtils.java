package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.dto.controller.response.*;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewSurvey;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewSearchDto;

import static bbangduck.bd.bbangduck.global.common.NullCheckUtils.isNotNull;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * 작성자 : 정구민 <br><br>
 * <p>
 * Review 응답 시 여러 Controller 에서 동시에 사용될 수 있는 부분에 대해
 * 코드 재사용율을 높이기 위해 구현한 utility class
 */
public class ReviewResponseUtils {
    public static ReviewResponseDto convertReviewToResponseDto(Review review, Member currentMember, boolean existsReviewLike, long periodForAddingSurveys) {
        ReviewSurvey reviewSurvey = review.getReviewSurvey();

        switch (review.getReviewType()) {
            case BASE:
                return isNotNull(reviewSurvey) ?
                        SimpleAndSurveyReviewResponseDto.convert(review, currentMember, existsReviewLike, periodForAddingSurveys) :
                        SimpleReviewResponseDto.convert(review, currentMember, existsReviewLike, periodForAddingSurveys);
            case DETAIL:
                return isNotNull(reviewSurvey) ?
                        DetailAndSurveyReviewResponseDto.convert(review, currentMember, existsReviewLike, periodForAddingSurveys) :
                        DetailReviewResponseDto.convert(review, currentMember, existsReviewLike, periodForAddingSurveys);
            default:
                return null;
        }
    }

    public static String getThemeReviewListNextPageUrlString(Long themeId, ReviewSearchDto searchDto, long totalPagesCount) {
        int nextPageNum = searchDto.getNextPageNum();
        if (nextPageNum <= totalPagesCount) {
            return linkTo(methodOn(ThemeReviewApiController.class).getReviewList(themeId, null, null, null)).toUriComponentsBuilder()
                    .queryParam("pageNum", searchDto.getNextPageNum())
                    .queryParam("amount", searchDto.getAmount())
                    .queryParam("sortCondition", searchDto.getSortCondition()).toUriString();
        }
        return null;
    }

    public static String getThemeReviewListPrevPageUriString(Long themeId, ReviewSearchDto searchDto, long totalPagesCount) {
        Integer prevPageNum = searchDto.getPrevPageNum();
        if (prevPageNum != null && totalPagesCount >= prevPageNum) {
            return linkTo(methodOn(ThemeReviewApiController.class).getReviewList(themeId, null, null, null)).toUriComponentsBuilder()
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
