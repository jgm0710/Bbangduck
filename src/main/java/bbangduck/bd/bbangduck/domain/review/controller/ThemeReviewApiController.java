package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.dto.controller.request.ReviewCreateRequestDto;
import bbangduck.bd.bbangduck.domain.review.dto.controller.request.ThemeReviewSearchRequestDto;
import bbangduck.bd.bbangduck.domain.review.dto.controller.response.ReviewResponseDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewSearchDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.service.ReviewApplicationService;
import bbangduck.bd.bbangduck.domain.review.service.ReviewLikeService;
import bbangduck.bd.bbangduck.domain.review.service.ReviewService;
import bbangduck.bd.bbangduck.global.common.PaginationResultResponseDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.ThrowUtils;
import bbangduck.bd.bbangduck.global.config.properties.ReviewProperties;
import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
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
 * <p>
 * 테마와 관련된 리뷰 요청 API 를 구현한 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/themes/{themeId}/reviews")
public class ThemeReviewApiController {

    private final ReviewApplicationService reviewApplicationService;

    private final ReviewService reviewService;

    private final ReviewValidator reviewValidator;

    private final ReviewLikeService reviewLikeService;

    private final ReviewProperties reviewProperties;

    /**
     * 기능 테스트
     * - 201
     * - 응답 코드, 메세지 확인
     * - 문서화 o
     * <p>
     * - 친구를 등록하지 않을 경우도 요청 성공
     * <p>
     * 실패 테스트
     * - validation - bad request o
     * -- 클리어를 했는데 클리어 시간을 입력하지 않은 경우 o
     * -- 클리어하지 않았는데 클리어 시간을 기입한 경우 o
     * -- 요청 시 아무런 정보도 기입하지 않았을 경우 o
     * -- 함께 플레이한 친구의 수가 제한된 수보다 많을 경우 (친구 수 제한은 properties 를 통해 관리) o
     * -- todo : 플레이 시간 10 시간 이상일 경우 test 미완
     * <p>
     * <p>
     * - 인증되지 않은 사용자가 리뷰를 생성할 경우 - unauthorized o
     * - 탈퇴한 회원이 리뷰를 생성할 경우 - forbidden o
     * <p>
     * - service 실패 o
     * -- 리뷰를 생성할 테마가 삭제된 테마일 경우 - bad request o
     * -- 테마를 찾을 수 없는 경우 - not found o
     * -- 함께한 친구로 등록하는 친구가 인증된 회원가 실제 친구 관계가 아닐 경우 - bad request o
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> createReview(
            @PathVariable Long themeId,
            @RequestBody @Valid ReviewCreateRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        reviewValidator.validateCreateView(requestDto, errors);

        Long createdReviewId = reviewApplicationService.createReview(currentMember.getId(), themeId, requestDto.toServiceDto());
        URI linkToGetReviewsUri = linkTo(methodOn(ReviewApiController.class).getReview(createdReviewId, currentMember)).toUri();

        return ResponseEntity.created(linkToGetReviewsUri).build();
    }

    @GetMapping
    public ResponseEntity<PaginationResultResponseDto<ReviewResponseDto>> getReviewList(
            @PathVariable Long themeId,
            @ModelAttribute @Valid ThemeReviewSearchRequestDto requestDto,
            BindingResult bindingResult,
            @CurrentUser Member currentMember
    ) {
        ThrowUtils.hasErrorsThrow(ResponseStatus.GET_THEME_REVIEW_LIST_NOT_VALID, bindingResult);

        ReviewSearchDto reviewSearchDto = requestDto.toServiceDto();
        QueryResults<Review> reviewQueryResults = reviewService.getThemeReviewList(themeId, reviewSearchDto);

        List<Review> findReviews = reviewQueryResults.getResults();
        List<ReviewResponseDto> reviewResponseDtos = findReviews.stream().map(review -> {
            boolean existsReviewLike = getExistsReviewLike(review.getId(), currentMember);
            return convertReviewToResponseDto(review, currentMember, existsReviewLike, reviewProperties.getPeriodForAddingSurveys());
        }).collect(Collectors.toList());

        long totalResultsCount = reviewQueryResults.getTotal();


        PaginationResultResponseDto<ReviewResponseDto> result = new PaginationResultResponseDto<>(
                reviewResponseDtos,
                requestDto.getPageNum(),
                requestDto.getAmount(),
                totalResultsCount
        );

        return ResponseEntity.ok(result);
    }

    private boolean getExistsReviewLike(Long reviewId, Member currentMember) {
        if (currentMember != null) {
            return reviewLikeService.getExistsReviewLike(currentMember.getId(), reviewId);
        }
        return false;
    }

}
