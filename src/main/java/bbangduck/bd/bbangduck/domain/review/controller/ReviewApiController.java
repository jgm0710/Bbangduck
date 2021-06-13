package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.controller.dto.*;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.exception.ReviewCreatedByOtherMembersException;
import bbangduck.bd.bbangduck.domain.review.service.ReviewLikeService;
import bbangduck.bd.bbangduck.domain.review.service.ReviewService;
import bbangduck.bd.bbangduck.global.common.ResponseDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.config.properties.ReviewProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static bbangduck.bd.bbangduck.domain.review.controller.ReviewResponseUtils.convertReviewToResponseDto;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * 작성자 : 정구민 <br><br>
 * <p>
 * 리뷰 자체에 대한 요청 API 를 구현하기 위한 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews/{reviewId}")
public class ReviewApiController {

    private final ReviewService reviewService;

    private final ReviewLikeService reviewLikeService;

    private final ReviewValidator reviewValidator;

    private final ReviewProperties reviewProperties;

    // TODO: 2021-06-13 test
    /**
     * 기능 테스트
     * - 201
     * - 응답 코드 및 메시지 확인
     * - 문서화
     *
     * 오류 테스트
     * - validation
     * -- 파일 저장소 ID 는 있으나 파일 이름이 없는 경우
     * -- 파일 이름이 있으나 파일 저장소 ID 가 없는 경우
     * - 다른 회원이 생성한 리뷰에 상세를 등록하는 경우
     * - 인증되지 않은 사용자가 접근하는 경우
     * - 탈퇴된 사용자가 접근하는 경우
     */
    @PostMapping("/details")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<Object>> addDetailToReview(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewDetailCreateRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        reviewValidator.validateAddDetailToReview(requestDto, errors);

        Review findReview = reviewService.getReview(reviewId);
        checkIsReviewCreatedByMe(currentMember, findReview, ResponseStatus.ADD_DETAIL_TO_REVIEW_CREATED_BY_OTHER_MEMBERS);

        reviewService.addDetailToReview(reviewId, requestDto.toServiceDto());
        URI linkToGetReviewsUri = linkTo(methodOn(ReviewApiController.class).getReview(reviewId, currentMember)).toUri();

        return ResponseEntity.created(linkToGetReviewsUri).body(new ResponseDto<>(ResponseStatus.ADD_SURVEY_TO_REVIEW_SUCCESS, null));
    }

    // TODO: 2021-06-13 기능 삭제
    // TODO: 2021-06-13 test
    /**
     * 기능 테스트
     * - 204
     * - 응답 코드, 메시지 확인
     *
     * 실패 테스트
     * - validation
     * -- 파일 저장소 ID 는 있으나 파일 이름이 없는 경우
     * -- 파일 이름이 있으나 파일 저장소 ID 가 없는 경우
     * - 다른 회원이 생성한 리뷰의 리뷰 상세를 수정하는 경우
     * - 인증되지 않은 사용자가 접근하는 경우
     * - 탈퇴된 사용자가 접근하는 경우
     */
    @PutMapping("/details")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<Object>> updateDetailFromReview(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewDetailUpdateRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        reviewValidator.validateUpdateDetailFromReview(requestDto, errors);

        Review findReview = reviewService.getReview(reviewId);
        checkIsReviewCreatedByMe(currentMember, findReview, ResponseStatus.UPDATE_DETAIL_FROM_REVIEW_CREATED_BY_OTHER_MEMBERS);

        reviewService.updateDetailFromReview(reviewId, requestDto.toServiceDto());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ResponseDto<>(ResponseStatus.UPDATE_DETAIL_FROM_REVIEW_SUCCESS, null));
    }

    // TODO: 2021-06-14 리뷰에 리뷰 상세와 설문 동시에 추가하는 api 구현

    @GetMapping
    public ResponseEntity<ResponseDto<ReviewResponseDto>> getReview(
            @PathVariable Long reviewId,
            @CurrentUser Member currentMember
    ) {
        Review findReview = reviewService.getReview(reviewId);
        boolean existsReviewLike = getExistsReviewLike(reviewId, currentMember);

        ReviewResponseDto reviewResponseDto = convertReviewToResponseDto(findReview, currentMember, existsReviewLike, reviewProperties.getPeriodForAddingSurveys());

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.GET_REVIEW_SUCCESS, reviewResponseDto));
    }

    private boolean getExistsReviewLike(Long reviewId, Member currentMember) {
        if (currentMember != null) {
            return reviewLikeService.getExistsReviewLike(currentMember.getId(), reviewId);
        }
        return false;
    }

    // TODO: 2021-05-22 리뷰 수정 기능 구현
    /**
     * 기능 테스트 - 문서화
     * - 204
     * - 응답 코드 및 메세지
     *
     * 요청 실패 경우
     * - Validation - bad request
     * -- 간단 리뷰 검증 o
     * -- 상세 리뷰 검증 o
     * -- 리뷰에 등록하는 친구가 5명 이상일 경우 o
     * -- clearYN 이 false 이면 clearTime 이 기입되지 않아도 됨 o
     * -- clearYN 이 false 이면 clearTime 이 기입되지 않아도 됨 o
     * -- 코멘트가 3000자를 넘긴 경우
     *
     * - 리뷰에 등록하는 친구가 실제 친구 관계가 아닐 경우 - bad request o
     * - 다른 회원이 생성한 리뷰를 수정하는 경우 - forbidden o
     * - 삭제된 리뷰일 경우 수정 불가  - bad request o
     * - 인증되지 않은 회원이 리뷰 수정 - unauthorized o
     * - 탈퇴된 회원이 리뷰 수정 - forbidden o
     * - 리뷰를 찾을 수 없는 경우 o
     */
    @PutMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<Object>> updateReview(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewUpdateRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        reviewValidator.validateUpdateReview(requestDto, errors);

        Review review = reviewService.getReview(reviewId);
        checkIsReviewCreatedByMe(currentMember, review, ResponseStatus.UPDATE_REVIEW_CREATED_BY_OTHER_MEMBERS);

        reviewService.updateReview(reviewId, requestDto.toServiceDto());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ResponseDto<>(ResponseStatus.UPDATE_REVIEW_SUCCESS, null));
    }

    private void checkIsReviewCreatedByMe(Member currentMember, Review review, ResponseStatus responseStatus) {
        Member reviewMember = review.getMember();
        if (!reviewMember.getId().equals(currentMember.getId())) {
            throw new ReviewCreatedByOtherMembersException(responseStatus);
        }
    }


    @PostMapping("/surveys")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<Object>> addSurveyToReview(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewSurveyCreateRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        reviewValidator.validateAddSurveyToReview(requestDto, errors);

        Review findReview = reviewService.getReview(reviewId);
        checkIsReviewCreatedByMe(currentMember, findReview, ResponseStatus.ADD_SURVEYS_TO_REVIEWS_CREATED_BY_OTHER_MEMBERS);

        reviewService.addSurveyToReview(reviewId, requestDto.toServiceDto());

        URI linkToGetReviewUri = linkTo(methodOn(ReviewApiController.class).getReview(reviewId, currentMember)).toUri();
        return ResponseEntity.created(linkToGetReviewUri).body(new ResponseDto<>(ResponseStatus.ADD_SURVEY_TO_REVIEW_SUCCESS, null));
    }

    @PutMapping("/surveys")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<Object>> updateSurveyFromReview(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewSurveyUpdateRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        reviewValidator.validateUpdateSurveyFromReview(requestDto, errors);

        Review findReview = reviewService.getReview(reviewId);
        checkIsReviewCreatedByMe(currentMember, findReview, ResponseStatus.UPDATE_SURVEY_FROM_REVIEW_CREATED_BY_OTHER_MEMBERS);

        reviewService.updateSurveyFromReview(reviewId, requestDto.toServiceDto());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ResponseDto<>(ResponseStatus.UPDATE_SURVEY_FROM_REVIEW_SUCCESS, null));
    }

    // TODO: 2021-05-22 리뷰 삭제 기능 구현

    // TODO: 2021-06-07 리뷰 좋아요 등록 기능 구현

    // TODO: 2021-06-07 리뷰 좋아요 등록 해제 기능 구현
}
