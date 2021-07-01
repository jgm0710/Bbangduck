package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRoomEscapeRecodesOpenStatus;
import bbangduck.bd.bbangduck.domain.friend.service.MemberFriendService;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.review.dto.controller.request.MemberReviewSearchRequestDto;
import bbangduck.bd.bbangduck.domain.review.dto.controller.response.ReviewResponseDto;
import bbangduck.bd.bbangduck.domain.review.dto.controller.response.PaginationResponseDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewSearchDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.exception.MemberRoomEscapeRecodesAreNotOpenException;
import bbangduck.bd.bbangduck.domain.review.exception.MemberRoomEscapeRecodesAreOnlyFriendOpenException;
import bbangduck.bd.bbangduck.domain.review.service.ReviewLikeService;
import bbangduck.bd.bbangduck.domain.review.service.ReviewService;
import bbangduck.bd.bbangduck.global.common.ResponseDto;
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

import static bbangduck.bd.bbangduck.domain.review.controller.ReviewResponseUtils.*;
import static bbangduck.bd.bbangduck.global.common.ThrowUtils.hasErrorsThrow;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

    private final MemberFriendService memberFriendService;

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
    @GetMapping
    public ResponseEntity<ResponseDto<PaginationResponseDto<Object>>> getMemberReviewList(
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
                    boolean isFriend = memberFriendService.isFriend(memberId, currentMember.getId());
                    if (!isFriend) {
                        throw new MemberRoomEscapeRecodesAreOnlyFriendOpenException();
                    }
                }
        }


        ReviewSearchDto reviewSearchDto = requestDto.toServiceDto();
        QueryResults<Review> reviewQueryResults = reviewService.getMemberReviewList(memberId, reviewSearchDto);

        List<Review> findReviews = reviewQueryResults.getResults();
        List<ReviewResponseDto> reviewResponseDtos = convertReviewsToReviewResponseDtos(currentMember, findReviews, reviewProperties.getPeriodForAddingSurveys());

        long totalPagesCount = calculateTotalPagesCount(reviewQueryResults.getTotal(), reviewSearchDto.getAmount());

        PaginationResponseDto<Object> paginationResponseDto = PaginationResponseDto.builder()
                .list(reviewResponseDtos)
                .nowPageNum(reviewSearchDto.getPageNum())
                .amount(reviewSearchDto.getAmount())
                .totalPagesCount(totalPagesCount)
                .prevPageUrl(getMemberReviewListPrevPageUriString(memberId, reviewSearchDto, totalPagesCount))
                .nextPageUrl(getMemberReviewListNextPageUriString(memberId, reviewSearchDto, totalPagesCount))
                .build();

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.GET_MEMBER_REVIEW_LIST_SUCCESS, paginationResponseDto));
    }

    /**
     * FIXME: 2021-06-15 좋아요 목록 조회 한 방 쿼링해서 리펙토링하기
     * 테마 리뷰 목록도 연계되어 있음
     */
    private List<ReviewResponseDto> convertReviewsToReviewResponseDtos(Member currentMember, List<Review> findReviews, long periodForAddingSurveys) {
        return findReviews.stream().map(review -> {
            boolean existsReviewLike = getExistsReviewLike(review.getId(), currentMember);
            return convertReviewToResponseDto(review, currentMember, existsReviewLike, periodForAddingSurveys);
        }).collect(Collectors.toList());
    }

    private String getMemberReviewListPrevPageUriString(Long memberId, ReviewSearchDto searchDto, long totalPagesCount) {
        if (prevPageExists(totalPagesCount, searchDto.getPrevPageNum())) {
            return linkTo(methodOn(MemberReviewApiController.class).getMemberReviewList(memberId, null, null, null))
                    .toUriComponentsBuilder()
                    .queryParam("pageNum", searchDto.getPrevPageNum())
                    .queryParam("amount", searchDto.getAmount())
                    .queryParam("searchType", searchDto.getSearchType())
                    .toUriString();
        }
        return null;
    }

    private String getMemberReviewListNextPageUriString(Long memberId, ReviewSearchDto searchDto, long totalPagesCount) {
        if (nextPageExists(totalPagesCount, searchDto.getNextPageNum())) {
            return linkTo(methodOn(MemberReviewApiController.class).getMemberReviewList(memberId,null,null,null))
                    .toUriComponentsBuilder()
                    .queryParam("pageNum", searchDto.getNextPageNum())
                    .queryParam("amount", searchDto.getAmount())
                    .queryParam("searchType", searchDto.getSearchType())
                    .toUriString();
        }
        return null;
    }

    private boolean getExistsReviewLike(Long reviewId, Member currentMember) {
        if (currentMember != null) {
            return reviewLikeService.getExistsReviewLike(currentMember.getId(), reviewId);
        }
        return false;
    }

}
