package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.dto.controller.request.*;
import bbangduck.bd.bbangduck.domain.review.dto.controller.response.ReviewResponseDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.exception.ReviewCreatedByOtherMembersException;
import bbangduck.bd.bbangduck.domain.review.service.*;
import bbangduck.bd.bbangduck.global.config.properties.ReviewProperties;
import lombok.RequiredArgsConstructor;
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

    private final ReviewApplicationService reviewApplicationService;

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
     * <p>
     * - 다른 회원이 생성한 리뷰에 상세를 등록하는 경우 - forbidden
     * - 인증되지 않은 사용자가 접근하는 경우 - unauthorized
     * - 탈퇴된 사용자가 접근하는 경우 - forbidden
     * <p>
     * - service 실패
     * -- 리뷰가 삭제된 리뷰일 경우 - bad request
     * -- 리뷰를 찾을 수 없는 경우 - not found
     * -- 이미 리뷰 상세가 등록되어 있는 리뷰일 경우 -bad request
     */
    @PostMapping("/details")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> addDetailToReview(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewDetailCreateRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        reviewValidator.validateAddDetailToReview(requestDto, errors);

        Review findReview = reviewService.getReview(reviewId);
        checkIsReviewCreatedByMe(currentMember, findReview);

        reviewApplicationService.addDetailToReview(reviewId, currentMember.getId(), requestDto.toServiceDto());
        ReviewResponseDto result = reviewApplicationService.getReview(reviewId, currentMember.getId());
        URI linkToGetReviewsUri = getLinkToGetReviewsUri(reviewId, currentMember);

        return ResponseEntity.created(linkToGetReviewsUri).body(result);
    }

    private URI getLinkToGetReviewsUri(@PathVariable Long reviewId, @CurrentUser Member currentMember) {
        return linkTo(methodOn(ReviewApiController.class).getReview(reviewId, currentMember)).toUri();
    }

    // TODO: 2021-06-13 기능 삭제, 테스트 미완

    /**
     * 기능 테스트 - 문서화 필요
     * - 204
     * - 응답 코드, 메시지 확인
     * <p>
     * 실패 테스트
     * - validation
     * -- 파일 저장소 ID 는 있으나 파일 이름이 없는 경우
     * -- 파일 이름이 있으나 파일 저장소 ID 가 없는 경우
     * <p>
     * - 다른 회원이 생성한 리뷰의 리뷰 상세를 수정하는 경우
     * - 인증되지 않은 사용자가 접근하는 경우
     * - 탈퇴된 사용자가 접근하는 경우
     */
    // TODO: 2021-07-10 실제로 사용하는지 확인
    // FIXME: 2021-07-11 실제로 사용하지 않음 -> 주석 처리
    @PutMapping("/details")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> updateDetailFromReview(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewDetailUpdateRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        reviewValidator.validateUpdateDetailFromReview(requestDto, errors);

        Review findReview = reviewService.getReview(reviewId);
        checkIsReviewCreatedByMe(currentMember, findReview);

        reviewService.updateDetailFromReview(reviewId, requestDto.toServiceDto());

        return ResponseEntity.noContent().build();
    }

    /**
     * 문서화 완료, 테스트 미완
     * <p>
     * 기능 테스트
     * -201
     * -응답 코드, 메세지 확인
     * <p>
     * TODO: 2021-06-14 실패 테스트 미완
     * 실패 테스트
     * - validation
     * -- 이미지를 올바르게 기입하지 않은 경우
     * --- 파일 저장소 id 는 있으나 파일 이름은 없는 경우
     * --- 파일 이름은 있으나 파일 저장소 id 는 없는 경우
     * -- 장르코드를 기입하지 않은 경우
     * -- 장르코드를 정해진 개수보다 많이 기입한 경우
     * -- 난이도, 공포도, 활동성, 만족도 등을 기입하지 않은 경우
     * <p>
     * - 다른 회원이 생성한 리뷰에 리뷰 상세 및 설문을 추가하는 경우
     * - 인증되지 않은 경우
     * - 탈퇴한 사용자인 경우
     * <p>
     * - service 실패
     * -- 리뷰를 찾을 수 없는 경우
     * -- 장르를 찾을 수 없는 경우
     * -- 이미 리뷰 상세가 등록된 설문일 경우
     * -- 이미 설문이 등록된 리뷰일 경우
     * -- 설문 등록 가능 기간이 지난 경우
     */
    @PostMapping("/details-and-surveys")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> addDetailAndSurveyToReview(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewDetailAndSurveyCreateDtoRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        reviewValidator.validateAddDetailAndSurveyToReview(requestDto, errors);

        reviewApplicationService.addDetailToReview(reviewId, currentMember.getId(), requestDto.toDetailServiceDto());
        reviewApplicationService.addSurveyToReview(reviewId, currentMember.getId(), requestDto.toSurveyServiceDto());
        ReviewResponseDto result = reviewApplicationService.getReview(reviewId, currentMember.getId());

        URI linkToGetReviewsUri = getLinkToGetReviewsUri(reviewId, currentMember);

        return ResponseEntity.created(linkToGetReviewsUri).body(result);
    }

    /**
     * 테스느 완료, 문서화 완료
     */
    @GetMapping
    public ResponseEntity<ReviewResponseDto> getReview(
            @PathVariable Long reviewId,
            @CurrentUser Member currentMember
    ) {
        Review findReview = reviewService.getReview(reviewId);
        boolean existsReviewLike = getExistsReviewLike(reviewId, currentMember);
        boolean possibleOfAddReviewSurvey = reviewService.isPossibleOfAddReviewSurvey(findReview.getRegisterTimes());

        ReviewResponseDto reviewResponseDto = convertReviewToResponseDto(findReview, currentMember, existsReviewLike, possibleOfAddReviewSurvey);

        return ResponseEntity.ok(reviewResponseDto);
    }

    private boolean getExistsReviewLike(Long reviewId, Member currentMember) {
        if (currentMember != null) {
            return reviewLikeService.isMemberLikeToReview(currentMember.getId(), reviewId);
        }
        return false;
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> updateReview(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewUpdateRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        reviewValidator.validateUpdateReview(requestDto, errors);

        reviewApplicationService.updateReview(reviewId, currentMember.getId(), requestDto.toServiceDto());

        return ResponseEntity.noContent().build();
    }

    private void checkIsReviewCreatedByMe(Member currentMember, Review review) {
        Member reviewMember = review.getMember();
        if (!reviewMember.getId().equals(currentMember.getId())) {
            throw new ReviewCreatedByOtherMembersException();
        }
    }

    @PostMapping("/surveys")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> addSurveyToReview(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewSurveyCreateRequestDto requestDto,
            Errors errors,
            @CurrentUser Member currentMember
    ) {
        reviewValidator.validateAddSurveyToReview(requestDto, errors);

        reviewApplicationService.addSurveyToReview(reviewId, currentMember.getId(), requestDto.toServiceDto());
        ReviewResponseDto result = reviewApplicationService.getReview(reviewId, currentMember.getId());

        URI linkToGetReviewUri = getLinkToGetReviewsUri(reviewId, currentMember);

        return ResponseEntity.created(linkToGetReviewUri).body(result);
    }

//    /**
//     * 문서화 완료, 테스트 완료
//     */
//    @PutMapping("/surveys")
//    @PreAuthorize("hasRole('ROLE_USER')")
//    public ResponseEntity<Object> updateSurveyFromReview(
//            @PathVariable Long reviewId,
//            @RequestBody @Valid ReviewSurveyUpdateRequestDto requestDto,
//            Errors errors,
//            @CurrentUser Member currentMember
//    ) {
//        reviewValidator.validateUpdateSurveyFromReview(requestDto, errors);
//
//        Review findReview = reviewService.getReview(reviewId);
//        checkIsReviewCreatedByMe(currentMember, findReview);
//
//        reviewService.updateSurveyFromReview(reviewId, requestDto.toServiceDto());
//
//        return ResponseEntity.noContent().build();
//    }

    /**
     * 문서화 완료, 테스트 미완
     * <p>
     * 기능 테스트 o
     * - 200
     * - 응답 코드, 메시지 확인
     * <p>
     * 실패 테스트
     * todo : 실패 테스트 미완
     * - 다른 회원이 생성한 리뷰를 삭제하는 경우 - forbidden
     * - 인증되지 않은 경우 - unauthorized
     * - 탈퇴된 사용자가 접근하는 경우 - forbidden
     * <p>
     * - service 실패 테스트
     * -- 리뷰를 찾을 수 없는 경우 - not found
     * -- 이미 삭제된 리뷰일 경우 - bad request
     */
    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> deleteReview(
            @PathVariable Long reviewId,
            @CurrentUser Member currentMember
    ) {
        reviewApplicationService.deleteReview(currentMember.getId(),reviewId);

        return ResponseEntity.noContent().build();
    }

    /**
     * 문서화 완료, 테스트 미완
     * <p>
     * 기능 테스트 o
     * - 200
     * - 응답 코드, 메시지 확인
     * - 문서화 x
     * <p>
     * todo : 실패 테스트 미완
     * 실패 테스트
     * - 인증되지 않은 경우 - unauthorized
     * - 탈퇴된 사용자일 경우  - forbidden
     * <p>
     * - service 실패 테스트
     * -- 이미 좋아요가 등록된 경우 - conflict
     * -- 회원을 찾을 수 없는 경우 - not found
     * -- 리뷰를 찾을 수 없는 경우 - not found
     * -- 자신이 생성한 리뷰에 좋아요를 등록하는 경우 - bad request
     */
    @PostMapping("/likes")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> addLikeToReview(
            @PathVariable Long reviewId,
            @CurrentUser Member currentMember
    ) {
        reviewApplicationService.addLikeToReview(currentMember.getId(), reviewId);

        URI linkToGetReview = linkTo(methodOn(ReviewApiController.class).getReview(reviewId, currentMember)).toUri();

        return ResponseEntity.created(linkToGetReview).build();
    }

    /**
     * 문서화 완료, 테스트 미완
     * 기능 테스트 o
     * - 200
     * - 응답 코드, 메세지 확인
     * - 문서화 x
     * <p>
     * 실패 테스트
     * todo : 실패 테스트 미완
     * - service 실패
     * -- 리뷰에 좋아요가 등록되어 있지 않은 경우
     * -- 회원을 찾을 수 없는 경우
     * -- 리뷰를 찾을 수 없는 경우
     */
    @DeleteMapping("/likes")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> removeLikeFromReview(
            @PathVariable Long reviewId,
            @CurrentUser Member currentMember
    ) {
        reviewApplicationService.removeLikeFromReview(currentMember.getId(), reviewId);

        return ResponseEntity.noContent().build();
    }

}
