package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewCreateRequestDto;
import bbangduck.bd.bbangduck.domain.review.service.ReviewService;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.ThrowUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static bbangduck.bd.bbangduck.global.common.ThrowUtils.hasErrorsThrow;

// TODO: 2021-05-23 주석 달기
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/themes/{themeId}/review")
public class ThemeReviewApiController {

    private final ReviewService reviewService;

    private final ReviewValidator reviewValidator;

    // TODO: 2021-05-22 리뷰 생성 기능 구현
    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity createReview(
            @PathVariable Long themeId,
            @RequestBody @Valid ReviewCreateRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        reviewValidator.validateCreateView(requestDto, errors);

        reviewService.createReview(currentMember.getId(), themeId, requestDto.toServiceDto());
        return null;

    }

    // TODO: 2021-05-22 테마별 리뷰 목록 기능 구현

    // TODO: 2021-05-22 특정 회원이 작성한 리뷰 목록 기능 구현

    // TODO: 2021-05-22 리뷰 수정 기능 구현

    // TODO: 2021-05-22 리뷰 삭제 기능 구현

}
