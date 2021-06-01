package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewCreateRequestDto;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.review.service.ReviewService;
import bbangduck.bd.bbangduck.global.common.ResponseDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.net.URI;

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

    private ResponseStatus getCreateReviewResponseStatus(ReviewType reviewType) {
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

    @GetMapping
    public ResponseEntity getReviewList(
            @PathVariable Long themeId
    ) {
        return null;
    }

    // TODO: 2021-05-22 테마별 리뷰 목록 기능 구현

}
