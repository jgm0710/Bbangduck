package bbangduck.bd.bbangduck.domain.review.entity;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewSurveyUpdateDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewUpdateBaseDto;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewHintUsageCount;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import bbangduck.bd.bbangduck.global.common.NullCheckUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 작성자 : 정구민 <br><br>
 * <p>
 * Review Entity
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntityDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    @Enumerated(EnumType.STRING)
    private ReviewType reviewType;

    private int recodeNumber;

    @Column(name = "clear_yn")
    private boolean clearYN;

    private LocalTime clearTime;

    @Enumerated(EnumType.STRING)
    private ReviewHintUsageCount hintUsageCount;

    private int rating;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewPlayTogether> reviewPlayTogethers = new ArrayList<>();

    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL)
    private ReviewDetail reviewDetail;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "review_survey_id")
    private ReviewSurvey reviewSurvey;

    private long likeCount;

    @Column(name = "delete_yn")
    private boolean deleteYN;

    @Builder
    public Review(Long id, Member member, Theme theme, ReviewType reviewType, int recodeNumber, boolean clearYN, LocalTime clearTime, ReviewHintUsageCount hintUsageCount, int rating, long likeCount, LocalDateTime registerTimes, LocalDateTime updateTimes, boolean deleteYN) {
        this.id = id;
        this.member = member;
        this.theme = theme;
        this.reviewType = reviewType;
        this.recodeNumber = recodeNumber;
        this.clearYN = clearYN;
        this.clearTime = clearTime;
        this.hintUsageCount = hintUsageCount;
        this.rating = rating;
        this.likeCount = likeCount;
        this.deleteYN = deleteYN;
        super.registerTimes = registerTimes;
        super.updateTimes = updateTimes;
    }

    public static Review create(Member member, Theme theme, int recodeNumber, ReviewCreateDto reviewCreateDto) {
        return Review.builder()
                .member(member)
                .theme(theme)
                .reviewType(ReviewType.BASE)
                .recodeNumber(recodeNumber)
                .clearYN(reviewCreateDto.isClearYN())
                .clearTime(reviewCreateDto.getClearTime())
                .hintUsageCount(reviewCreateDto.getHintUsageCount())
                .rating(reviewCreateDto.getRating())
                .likeCount(0)
                .deleteYN(false)
                .build();
    }

    public List<Member> getPlayTogetherMembers() {
        return this.reviewPlayTogethers.stream().map(ReviewPlayTogether::getMember).collect(Collectors.toList());
    }

    public void addPlayTogether(Member friend) {
        ReviewPlayTogether reviewPlayTogether = ReviewPlayTogether.builder()
                .review(this)
                .member(friend)
                .build();

        this.reviewPlayTogethers.add(reviewPlayTogether);
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Theme getTheme() {
        return theme;
    }

    public ReviewType getReviewType() {
        return reviewType;
    }

    public LocalTime getClearTime() {
        return clearTime;
    }

    public ReviewHintUsageCount getHintUsageCount() {
        return hintUsageCount;
    }

    public int getRating() {
        return rating;
    }

    public int getRecodeNumber() {
        return recodeNumber;
    }

    public boolean isClearYN() {
        return clearYN;
    }

    public boolean isMyReview(Member currentMember) {
        return currentMember != null && this.member.getId().equals(currentMember.getId());
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount--;
    }

    public void addReviewSurvey(ReviewSurvey reviewSurvey) {
        reviewSurvey.setReview(this);
        this.reviewSurvey = reviewSurvey;
    }

    public ReviewSurvey getReviewSurvey() {
        return reviewSurvey;
    }

    public void updateSurvey(ReviewSurveyUpdateDto reviewSurveyUpdateDto) {
        this.reviewSurvey.update(reviewSurveyUpdateDto);
    }

    public boolean isDeleteYN() {
        return deleteYN;
    }

    public void updateBase(ReviewUpdateBaseDto reviewUpdateBaseDto) {
        this.reviewType = reviewUpdateBaseDto.getReviewType();
        this.clearYN = reviewUpdateBaseDto.isClearYN();
        this.clearTime = reviewUpdateBaseDto.getClearTime();
        this.hintUsageCount = reviewUpdateBaseDto.getHintUsageCount();
        this.rating = reviewUpdateBaseDto.getRating();
    }

    public void delete() {
        this.deleteYN = true;
        this.recodeNumber = -1;
    }

    public ReviewDetail getReviewDetail() {
        return reviewDetail;
    }

    public void addReviewDetail(ReviewDetail reviewDetail) {
        this.reviewType = ReviewType.DETAIL;
        this.reviewDetail = reviewDetail;
        reviewDetail.setReview(this);
    }

    public void clearDetail() {
        if (NullCheckUtils.isNotNull(this.reviewDetail)) {
            this.reviewDetail.clearReview();
            this.reviewDetail = null;
        }
    }

    public List<ReviewPlayTogether> getReviewPlayTogetherEntities() {
        return reviewPlayTogethers;
    }

    public void clearPlayTogether() {
        this.reviewPlayTogethers.clear();
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
//                ", member=" + member +
//                ", theme=" + theme +
                ", reviewType=" + reviewType +
                ", recodeNumber=" + recodeNumber +
                ", clearYN=" + clearYN +
                ", clearTime=" + clearTime +
                ", hintUsageCount=" + hintUsageCount +
                ", rating=" + rating +
//                ", reviewPlayTogethers=" + reviewPlayTogethers +
//                ", reviewDetail=" + reviewDetail +
//                ", reviewSurvey=" + reviewSurvey +
                ", likeCount=" + likeCount +
                ", deleteYN=" + deleteYN +
                '}';
    }
}
