package bbangduck.bd.bbangduck.domain.review.entity;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewDetailRepository;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewPlayTogetherRepository;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewDetailUpdateDto;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewSurveyUpdateDto;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewUpdateDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static bbangduck.bd.bbangduck.global.common.NullCheckUtils.existsList;
import static bbangduck.bd.bbangduck.global.common.NullCheckUtils.isNotNull;

/**
 * 작성자 : 정구민 <br><br>
 * <p>
 * Review Entity
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntityDateTime {

    /**
     * 간단 리뷰 (공통 기입)
     */
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

    private int hintUsageCount;

    private int rating;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewPlayTogether> reviewPlayTogethers = new ArrayList<>();

//    /**
//     * 상세 리뷰
//     */
//    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
//    private List<ReviewImage> reviewImages = new ArrayList<>();
//
//    @Column(length = 3000)
//    private String comment;

    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL)
    private ReviewDetail reviewDetail;

    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL)
    private ReviewSurvey reviewSurvey;

    private long likeCount;

    @Column(name = "delete_yn")
    private boolean deleteYN;

    @Builder
    public Review(Long id, Member member, Theme theme, ReviewType reviewType, int recodeNumber, boolean clearYN, LocalTime clearTime, int hintUsageCount, int rating, long likeCount, LocalDateTime registerTimes, LocalDateTime updateTimes, boolean deleteYN) {
        this.id = id;
        this.member = member;
        this.theme = theme;
        this.reviewType = reviewType;
        this.recodeNumber = recodeNumber;
        this.clearYN = clearYN;
        this.clearTime = clearTime;
        this.hintUsageCount = hintUsageCount;
        this.rating = rating;
//        this.comment = comment;
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

    public int getHintUsageCount() {
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

    public void setReviewSurvey(ReviewSurvey reviewSurvey) {
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

    public void updateBase(ReviewUpdateDto reviewUpdateDto) {
        this.reviewType = reviewUpdateDto.getReviewType();
        this.clearYN = reviewUpdateDto.isClearYN();
        this.clearTime = reviewUpdateDto.getClearTime();
        this.hintUsageCount = reviewUpdateDto.getHintUsageCount();
        this.rating = reviewUpdateDto.getRating();
    }

    public List<ReviewPlayTogether> getReviewPlayTogetherEntities() {
        return reviewPlayTogethers;
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

    public void updateDetail(ReviewDetailUpdateDto reviewDetailUpdateDto) {
        this.reviewDetail.update(reviewDetailUpdateDto);
    }

    public void clearDetail(ReviewDetailRepository reviewDetailRepository) {
        if (isNotNull(this.reviewDetail)) {
            reviewDetailRepository.delete(this.reviewDetail);
            this.reviewDetail = null;
        }
    }

    public void clearPlayTogether(ReviewPlayTogetherRepository reviewPlayTogetherRepository) {
        if (existsList(this.reviewPlayTogethers)) {
            reviewPlayTogetherRepository.deleteInBatch(this.reviewPlayTogethers);
            this.reviewPlayTogethers.clear();
        }
    }
}
