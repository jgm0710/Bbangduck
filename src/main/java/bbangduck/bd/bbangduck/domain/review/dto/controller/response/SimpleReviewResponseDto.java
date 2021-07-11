package bbangduck.bd.bbangduck.domain.review.dto.controller.response;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewHintUsageCount;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 작성자 : 정구민 <br><br>
 * <p>
 * 리뷰 조회 시 해당 리뷰가 간단 리뷰일 경우 해당 Dto 를 동해서 Data 응답
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleReviewResponseDto implements ReviewResponseDto {

    private Long reviewId;

    private ReviewMemberSimpleInfoResponseDto writerInfo;

    private ReviewThemeSimpleInfoResponseDto themeInfo;

    private ReviewType reviewType;

    private Integer reviewRecodeNumber;

    private Boolean themeClearYN;

    private LocalTime themeClearTime;

    private ReviewHintUsageCount hintUsageCount;

    private Integer rating;

    private List<ReviewMemberSimpleInfoResponseDto> playTogetherFriends;

    private Long likeCount;

    private boolean myReview;

    private boolean like;

    private boolean possibleRegisterForSurveyYN;

    protected boolean surveyYN;

    private LocalDateTime registerTimes;

    private LocalDateTime updateTimes;


    public static SimpleReviewResponseDto convert(Review review, Member currentMember, boolean existReviewLike, boolean possibleOfAddReviewSurvey) {
        return new SimpleReviewResponseDto(review, currentMember, existReviewLike, possibleOfAddReviewSurvey);
    }

    protected SimpleReviewResponseDto(Review review, Member currentMember, boolean existReviewLike, boolean possibleOfAddReviewSurvey) {
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
        this.possibleRegisterForSurveyYN = possibleOfAddReviewSurvey;
        this.surveyYN = false;
        this.registerTimes = review.getRegisterTimes();
        this.updateTimes = review.getUpdateTimes();
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
