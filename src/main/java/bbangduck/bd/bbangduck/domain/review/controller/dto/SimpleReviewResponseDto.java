package bbangduck.bd.bbangduck.domain.review.controller.dto;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 조회 시 해당 리뷰가 간단 리뷰일 경우 해당 Dto 를 동해서 Data 응답
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleReviewResponseDto implements ReviewResponseDto{

    private Long reviewId;

    private ReviewMemberSimpleInfoResponseDto writerInfo;

    private ReviewThemeSimpleInfoResponseDto themeInfo;

    private ReviewType reviewType;

    private Integer reviewRecodeNumber;

    private Boolean themeClearYN;

    private LocalTime themeClearTime;

    private Integer hintUsageCount;

    private Integer rating;

    private List<ReviewMemberSimpleInfoResponseDto> playTogetherFriends;

    private Long likeCount;

    private boolean myReview;

    private boolean like;


    public static SimpleReviewResponseDto convert(Review review, Member currentMember, boolean existReviewLike) {
        return new SimpleReviewResponseDto(review, currentMember, existReviewLike);
    }

    protected SimpleReviewResponseDto(Review review,Member currentMember, boolean existReviewLike) {
        this.reviewId = review.getId();
        this.writerInfo = ReviewMemberSimpleInfoResponseDto.convert(review.getMember());
        this.themeInfo = ReviewThemeSimpleInfoResponseDto.convert(review.getTheme());
        this.reviewType = review.getReviewType();
        this.reviewRecodeNumber = review.getRecodeNumber();
        this.themeClearYN = review.isClearYN();
        this.themeClearTime = review.getClearTime();
        this.hintUsageCount = review.getHintUsageCount();
        this.rating = review.getRating();
        this.playTogetherFriends = convertReviewPlayTogetherFriends(review.getPlayTogetherMembers());
        this.likeCount = review.getLikeCount();
        this.myReview = review.isMyReview(currentMember);
        this.like = existReviewLike;
    }

    private List<ReviewMemberSimpleInfoResponseDto> convertReviewPlayTogetherFriends(List<Member> playTogetherMembers) {
        if (playTogetherMembersExists(playTogetherMembers)) {
            return playTogetherMembers.stream()
                        .map(ReviewMemberSimpleInfoResponseDto::convert)
                        .collect(Collectors.toList());
        }
        return null;
    }

    private boolean playTogetherMembersExists(List<Member> playTogetherMembers) {
        return playTogetherMembers != null && !playTogetherMembers.isEmpty();
    }
}
