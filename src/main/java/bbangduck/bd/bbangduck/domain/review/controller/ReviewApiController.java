package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.dto.controller.request.*;
import bbangduck.bd.bbangduck.domain.review.dto.controller.response.ReviewResponseDto;
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
public class ReviewApiController{

    private final ReviewService reviewService;

    private final ReviewLikeService reviewLikeService;

    private final ReviewValidator reviewValidator;

    private final ReviewProperties reviewProperties;

    /**
     * 문서화 완료, 테스트 미완
     * 기능 테스트 o
     * - 201 o
     * - 응답 코드 및 메시지 확인 o
     * - 문서화 0
     * <p>
     * TODO: 2021-06-13 요청 실패 테스트 미완
     * 오류 테스트
     * - validation - bad request
     * -- 파일 저장소 ID 는 있으나 파일 이름이 없는 경우
     * -- 파일 이름이 있으나 파일 저장소 ID 가 없는 경우
     *
     * - 다른 회원이 생성한 리뷰에 상세를 등록하는 경우 - forbidden
     * - 인증되지 않은 사용자가 접근하는 경우 - unauthorized
     * - 탈퇴된 사용자가 접근하는 경우 - forbidden
     *
     * - service 실패
     * -- 리뷰가 삭제된 리뷰일 경우 - bad request
     * -- 리뷰를 찾을 수 없는 경우 - not found
     * -- 이미 리뷰 상세가 등록되어 있는 리뷰일 경우 -bad request
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
        URI linkToGetReviewsUri = getLinkToGetReviewsUri(reviewId, currentMember);

        return ResponseEntity.created(linkToGetReviewsUri).body(new ResponseDto<>(ResponseStatus.ADD_DETAIL_TO_REVIEW_SUCCESS, null));
    }

    private URI getLinkToGetReviewsUri(@PathVariable Long reviewId, @CurrentUser Member currentMember) {
        return linkTo(methodOn(ReviewApiController.class).getReview(reviewId, currentMember)).toUri();
    }

    // TODO: 2021-06-13 기능 삭제
    // TODO: 2021-06-13 test
    /**
     * 기능 테스트 - 문서화 필요
     * - 204
     * - 응답 코드, 메시지 확인
     * <p>
     * 실패 테스트
     * - validation
     * -- 파일 저장소 ID 는 있으나 파일 이름이 없는 경우
     * -- 파일 이름이 있으나 파일 저장소 ID 가 없는 경우
     *
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

    /**
     * 문서화 완료, 테스트 미완
     *
     * 기능 테스트
     * -201
     * -응답 코드, 메세지 확인
     *
     * TODO: 2021-06-14 실패 테스트 미완
     * 실패 테스트
     * - validation
     * -- 이미지를 올바르게 기입하지 않은 경우
     * --- 파일 저장소 id 는 있으나 파일 이름은 없는 경우
     * --- 파일 이름은 있으나 파일 저장소 id 는 없는 경우
     * -- 장르코드를 기입하지 않은 경우
     * -- 장르코드를 정해진 개수보다 많이 기입한 경우
     * -- 난이도, 공포도, 활동성, 만족도 등을 기입하지 않은 경우
     *
     * - 다른 회원이 생성한 리뷰에 리뷰 상세 및 설문을 추가하는 경우
     * - 인증되지 않은 경우
     * - 탈퇴한 사용자인 경우
     *
     * - service 실패
     * -- 리뷰를 찾을 수 없는 경우
     * -- 장르를 찾을 수 없는 경우
     * -- 이미 리뷰 상세가 등록된 설문일 경우
     * -- 이미 설문이 등록된 리뷰일 경우
     * -- 설문 등록 가능 기간이 지난 경우
     */
    @PostMapping("/details-and-surveys")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<Object>> addDetailAndSurveyToReview(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewDetailAndSurveyCreateDtoRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        reviewValidator.validateAddDetailAndSurveyToReview(requestDto, errors);

        Review findReview = reviewService.getReview(reviewId);
        checkIsReviewCreatedByMe(currentMember, findReview, ResponseStatus.ADD_DETAIL_AND_SURVEY_TO_REVIEW_CREATED_BY_OTHER_MEMBERS);

        reviewService.addDetailAndSurveyToReview(reviewId, requestDto.toDetailServiceDto(), requestDto.toSurveyServiceDto());
        URI linkToGetReviewsUri = getLinkToGetReviewsUri(reviewId, currentMember);

        return ResponseEntity.created(linkToGetReviewsUri).body(new ResponseDto<>(ResponseStatus.ADD_DETAIL_AND_SURVEY_TO_REVIEW_SUCCESS, null));

    }

    /**
     * 테스느 완료, 문서화 완료
     */
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

    /**
     * 문서화 완료, 테스트 완료
     *
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


    /**
     * 테스트, 문서화 완료
     *
     * 기능 테스트
     * - 201
     * - 문서화 o
     *
     * 실패 테스트
     * - validation - bad request o
     * --체감 장르 수가 제한된 개수보다 많이 기입될 경우 o
     * --체감 난이도, 체감 공포도, 체감 활동성, 시나리오 만족도, 인테리어 만족도, o
     *   문제 구성 만족도를 기입하지 않은 경우
     *
     * - 다른 회원이 생성한 리뷰에 설문을 등록하는 경우 - forbidden o
     * - 인증되지 않은 사용자가 설문을 등록하는 경우 - unauthorized o
     * - 탈퇴된 사용자가 설문을 등록하는 경우  - forbidden o
     *
     * - service 실패
     * -- 리뷰가 삭제된 리뷰일 경우 - bad request o
     * -- 리뷰를 찾을 수 없는 경우 - not found o
     * -- 장르를 찾을 수 없는 경우 - not found o
     */
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

        URI linkToGetReviewUri = getLinkToGetReviewsUri(reviewId, currentMember);
        return ResponseEntity.created(linkToGetReviewUri).body(new ResponseDto<>(ResponseStatus.ADD_SURVEY_TO_REVIEW_SUCCESS, null));
    }

    /**
     * 문서화 완료, 테스트 완료
     */
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

    /**
     * 문서화 완료, 테스트 미완
     *
     * 기능 테스트 o
     * - 200
     * - 응답 코드, 메시지 확인
     *
     * 실패 테스트
     * todo : 실패 테스트 미완
     * - 다른 회원이 생성한 리뷰를 삭제하는 경우 - forbidden
     * - 인증되지 않은 경우 - unauthorized
     * - 탈퇴된 사용자가 접근하는 경우 - forbidden
     *
     * - service 실패 테스트
     * -- 리뷰를 찾을 수 없는 경우 - not found
     * -- 이미 삭제된 리뷰일 경우 - bad request
     */
    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<Object>> deleteReview(
            @PathVariable Long reviewId,
            @CurrentUser Member currentMember
    ) {
        Review findReview = reviewService.getReview(reviewId);
        checkIsReviewCreatedByMe(currentMember, findReview, ResponseStatus.DELETE_REVIEW_CREATED_BY_OTHER_MEMBERS);

        reviewService.deleteReview(reviewId);

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.DELETE_REVIEW_SUCCESS, null));
    }

    /**
     * 문서화 완료, 테스트 미완
     *
     * 기능 테스트 o
     * - 200
     * - 응답 코드, 메시지 확인
     * - 문서화 x
     *
     * todo : 실패 테스트 미완
     * 실패 테스트
     * - 인증되지 않은 경우 - unauthorized
     * - 탈퇴된 사용자일 경우  - forbidden
     *
     * - service 실패 테스트
     * -- 이미 좋아요가 등록된 경우 - conflict
     * -- 회원을 찾을 수 없는 경우 - not found
     * -- 리뷰를 찾을 수 없는 경우 - not found
     * -- 자신이 생성한 리뷰에 좋아요를 등록하는 경우 - bad request
     */
    @PostMapping("/likes")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<Object>> addLikeToReview(
            @PathVariable Long reviewId,
            @CurrentUser Member currentMember
    ) {
        reviewLikeService.addLikeToReview(currentMember.getId(), reviewId);

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.ADD_LIKE_TO_REVIEW_SUCCESS, null));
    }

    /**
     * 문서화 완료, 테스트 미완
     * 기능 테스트 o
     * - 200
     * - 응답 코드, 메세지 확인
     * - 문서화 x
     *
     * 실패 테스트
     * todo : 실패 테스트 미완
     * - service 실패
     * -- 리뷰에 좋아요가 등록되어 있지 않은 경우
     * -- 회원을 찾을 수 없는 경우
     * -- 리뷰를 찾을 수 없는 경우
     */
    @DeleteMapping("/likes")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<Object>> removeLikeFromReview(
            @PathVariable Long reviewId,
            @CurrentUser Member currentMember
    ) {
        reviewLikeService.removeLikeFromReview(currentMember.getId(), reviewId);

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.REMOVE_LIKE_FROM_REVIEW_SUCCESS, null));
    }



}
