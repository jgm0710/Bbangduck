package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.controller.dto.*;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.service.ReviewLikeService;
import bbangduck.bd.bbangduck.domain.review.service.ReviewService;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewSearchDto;
import bbangduck.bd.bbangduck.global.common.PaginationResponseDto;
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

    @GetMapping
    public ResponseEntity<ResponseDto<PaginationResponseDto<Object>>> getReviewList(
            @PathVariable Long themeId,
            @ModelAttribute ThemeReviewSearchRequestDto requestDto,
            @CurrentUser Member currentMember
    ) {
        ReviewSearchDto reviewSearchDto = requestDto.toServiceDto();
        QueryResults<Review> reviewQueryResults = reviewService.getThemeReviewList(themeId, reviewSearchDto);
        long totalPageCount = reviewQueryResults.getTotal();
        List<Review> findReviews = reviewQueryResults.getResults();

        List<ReviewResponseDto> reviewResponseDtos = findReviews.stream().map(review -> {
            boolean existsReviewLike = getExistsReviewLike(review.getId(), currentMember);
            return convertReviewToResponseDto(review, currentMember, existsReviewLike);
        }).collect(Collectors.toList());

        PaginationResponseDto<Object> reviewsPaginationResponseDto = PaginationResponseDto.builder()
                .list(reviewResponseDtos)
                .pageNum(requestDto.getPageNum())
                .amount(requestDto.getAmount())
                .totalPageCount(totalPageCount)
                .prevPage(getThemeReviewListPrevPageUriString(themeId, reviewSearchDto, currentMember))
                .nextPage(getThemeReviewListNextPageUrlString(themeId, reviewSearchDto, currentMember, totalPageCount))
                .build();

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.GET_REVIEW_LIST_SUCCESS, reviewsPaginationResponseDto));
    }

    private boolean getExistsReviewLike(Long reviewId, Member currentMember) {
        if (currentMember != null) {
            return reviewLikeService.getExistsReviewLike(currentMember.getId(), reviewId);
        }
        return false;
    }

    // TODO: 2021-05-22 테마별 리뷰 목록 기능 구현

}
