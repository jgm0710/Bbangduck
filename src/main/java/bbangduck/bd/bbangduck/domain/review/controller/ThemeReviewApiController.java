package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewCreateRequestDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewResponseDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ThemeReviewSearchRequestDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.service.ReviewLikeService;
import bbangduck.bd.bbangduck.domain.review.service.ReviewService;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewSearchDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewPaginationResponseDto;
import bbangduck.bd.bbangduck.global.common.ResponseDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static bbangduck.bd.bbangduck.domain.review.controller.ReviewResponseUtils.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 테마와 관련된 리뷰 요청 API 를 구현한 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/themes/{themeId}/reviews")
public class ThemeReviewApiController {

    private final ReviewService reviewService;

    private final ReviewValidator reviewValidator;

    private final ReviewLikeService reviewLikeService;

    // TODO: 2021-06-07 리뷰 생성 로직 변경
    // TODO: 2021-06-09 friendIds 5개 이상일 경우 validation 에러 발생
    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<Object>> createReview(
            @PathVariable Long themeId,
            @RequestBody @Valid ReviewCreateRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        reviewValidator.validateCreateView(requestDto, errors);

        Long createdReviewId = reviewService.createReview(currentMember.getId(), themeId, requestDto.toServiceDto());
        URI linkToGetReviewsUri = linkTo(methodOn(ReviewApiController.class).getReview(createdReviewId, currentMember)).toUri();
        ResponseStatus responseStatus = getCreateReviewResponseStatus(requestDto.getReviewType());

        assert responseStatus != null;
        return ResponseEntity.created(linkToGetReviewsUri).body(new ResponseDto<>(responseStatus, null));

    }

    // TODO: 2021-06-08 리뷰 목록 조회 로직 수정
    // TODO: 2021-06-08 페이지 넘버를 0보다 작게 입력한 경우나, 수량을 잘못 입력한 경우 테스트 구현
    @GetMapping
    public ResponseEntity<ResponseDto<ReviewPaginationResponseDto<Object>>> getReviewList(
            @PathVariable Long themeId,
            @ModelAttribute ThemeReviewSearchRequestDto requestDto,
            @CurrentUser Member currentMember
    ) {
        ReviewSearchDto reviewSearchDto = requestDto.toServiceDto();

        QueryResults<Review> reviewQueryResults = reviewService.getThemeReviewList(themeId, reviewSearchDto);
        long totalResultsCount = reviewQueryResults.getTotal();
        List<Review> findReviews = reviewQueryResults.getResults();

        List<ReviewResponseDto> reviewResponseDtos = findReviews.stream().map(review -> {
            boolean existsReviewLike = getExistsReviewLike(review.getId(), currentMember);
            return convertReviewToResponseDto(review, currentMember, existsReviewLike);
        }).collect(Collectors.toList());

        long totalPagesCount = calculateTotalPagesCount(totalResultsCount, reviewSearchDto.getAmount());

        ReviewPaginationResponseDto<Object> reviewsReviewPaginationResponseDto = ReviewPaginationResponseDto.builder()
                .list(reviewResponseDtos)
                .pageNum(requestDto.getPageNum())
                .amount(requestDto.getAmount())
                .totalPagesCount(totalPagesCount)
                .prevPageUrl(getThemeReviewListPrevPageUriString(themeId, reviewSearchDto, totalPagesCount))
                .nextPageUrl(getThemeReviewListNextPageUrlString(themeId, reviewSearchDto, totalPagesCount))
                .build();


        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.GET_REVIEW_LIST_SUCCESS, reviewsReviewPaginationResponseDto));
    }

    private boolean getExistsReviewLike(Long reviewId, Member currentMember) {
        if (currentMember != null) {
            return reviewLikeService.getExistsReviewLike(currentMember.getId(), reviewId);
        }
        return false;
    }

}
