package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.follow.service.FollowService;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRoomEscapeRecodesOpenStatus;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.review.dto.controller.request.MemberReviewSearchRequestDto;
import bbangduck.bd.bbangduck.domain.review.dto.controller.response.ReviewResponseDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewSearchDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.exception.MemberRoomEscapeRecodesAreNotOpenException;
import bbangduck.bd.bbangduck.domain.review.exception.MemberRoomEscapeRecodesAreOnlyFriendOpenException;
import bbangduck.bd.bbangduck.domain.review.service.ReviewLikeService;
import bbangduck.bd.bbangduck.domain.review.service.ReviewService;
import bbangduck.bd.bbangduck.global.common.PaginationResultResponseDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.config.properties.ReviewProperties;
import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static bbangduck.bd.bbangduck.domain.review.controller.ReviewResponseUtils.convertReviewToResponseDto;
import static bbangduck.bd.bbangduck.global.common.ThrowUtils.hasErrorsThrow;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원과 관련된 리뷰 요청 API 를 구현하기 위한 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}/reviews")
public class MemberReviewApiController{

    private final MemberService memberService;

    private final FollowService followService;

    private final ReviewService reviewService;

    private final ReviewLikeService reviewLikeService;

    private final ReviewProperties reviewProperties;

    /**
     * 문서화 완료, 테스트 미완
     *
     * 기능 테스트 o
     * - 자신의 리뷰 목록을 조회하는 경우 o
     * - 다른 회원의 리뷰 목록을 조회하는 경우 o
     * - 친구에게만 방탈출 기록을 공개한 회원의 기록을 친구가 조회하는 경우
     * - 간단 리뷰, 간단 및 설문 리뷰 잘 나오는지 확인 o
     * - 상세 리뷰, 상세 및 설문 리뷰 잘 나오는지 확인 o
     * - nextPageUrl 을 통해 다음 페이지 조회가 가능한지 확인 o
     * - prevPageUrl 을 통해 다음 페이지 조회가 가능한지 확인 o
     *
     * todo : 실패 테스트 미완
     * 실패 테스트
     * - validation
     * -- 1 page 미만의 페이지를 조회하는 경우
     * -- amount 가 1보다 작을 경우
     * -- amount 가 200보다 클 경우
     *
     * - 다른 회원의 방탈출 기록을 조회할 때, 해당 회원이 방탈출 기록을 공개하지 않는 회원일 경우 - conflict o
     * - 다른 회원의 방탈출 기록을 조회할 때, 해당 회원이 방탈출 기록을 친구에게만 공개했는데, 서로 친구 관계가 아닌 경우 - conflict o
     *
     * - service 실패
     * -- 회원을 찾을 수 없는 경우
     */
    /**
     * FIXME: 2021-06-15 좋아요 목록 조회 한 방 쿼링해서 리펙토링하기
     * 테마 리뷰 목록도 연계되어 있음
     */
    @GetMapping
    public ResponseEntity<PaginationResultResponseDto<ReviewResponseDto>> getMemberReviewList(
            @PathVariable Long memberId,
            @ModelAttribute @Valid MemberReviewSearchRequestDto requestDto,
            BindingResult bindingResult,
            @CurrentUser Member currentMember
    ) {
        hasErrorsThrow(ResponseStatus.GET_MEMBER_REVIEW_LIST_NOT_VALID, bindingResult);

        Member findMember = memberService.getMember(memberId);
        boolean myId = findMember.isMyId(currentMember.getId());
        MemberRoomEscapeRecodesOpenStatus roomEscapeRecodesOpenStatus = findMember.getRoomEscapeRecodesOpenStatus();

        switch (roomEscapeRecodesOpenStatus) {
            case CLOSE:
                if (!myId) {
                    throw new MemberRoomEscapeRecodesAreNotOpenException();
                }
                break;
            case ONLY_FRIENDS_OPEN:
                if (!myId) {
                    boolean isFriend = followService.isTwoWayFollowRelation(memberId, currentMember.getId());
                    if (!isFriend) {
                        throw new MemberRoomEscapeRecodesAreOnlyFriendOpenException();
                    }
                }
        }


        ReviewSearchDto reviewSearchDto = requestDto.toServiceDto();
        QueryResults<Review> reviewQueryResults = reviewService.getMemberReviewList(memberId, reviewSearchDto);

        List<Review> findReviews = reviewQueryResults.getResults();
        List<ReviewResponseDto> reviewResponseDtos = convertReviewsToReviewResponseDtos(currentMember, findReviews);

        PaginationResultResponseDto<ReviewResponseDto> result = new PaginationResultResponseDto<>(
                reviewResponseDtos,
                requestDto.getPageNum(),
                requestDto.getAmount(),
                reviewQueryResults.getTotal()
        );

        return ResponseEntity.ok(result);
    }

    private List<ReviewResponseDto> convertReviewsToReviewResponseDtos(Member currentMember, List<Review> findReviews) {
        return findReviews.stream().map(review -> {
            boolean existsReviewLike = getExistsReviewLike(review.getId(), currentMember);
            boolean possibleOfAddReviewSurvey = reviewService.isPossibleOfAddReviewSurvey(review.getRegisterTimes());
            return convertReviewToResponseDto(review, currentMember, existsReviewLike, possibleOfAddReviewSurvey);
        }).collect(Collectors.toList());
    }

    private boolean getExistsReviewLike(Long reviewId, Member currentMember) {
        if (currentMember != null) {
            return reviewLikeService.isMemberLikeToReview(currentMember.getId(), reviewId);
        }
        return false;
    }

}
