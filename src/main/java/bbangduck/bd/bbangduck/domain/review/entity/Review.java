package bbangduck.bd.bbangduck.domain.review.entity;

import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.model.emumerate.*;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewImageDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 작성자 : 정구민 <br><br>
 *
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

    /**
     * 상세 리뷰
     */
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewImage> reviewImages = new ArrayList<>();

    @Column(length = 3000)
    private String comment;

    /**
     * 상세 및 추가 설문 작성 리뷰
     */
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewPerceivedThemeGenre> perceivedThemeGenres = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Difficulty perceivedDifficulty;

    @Enumerated(EnumType.STRING)
    private HorrorGrade perceivedHorrorGrade;

    @Enumerated(EnumType.STRING)
    private Activity perceivedActivity;

    @Enumerated(EnumType.STRING)
    private Satisfaction scenarioSatisfaction;

    @Enumerated(EnumType.STRING)
    private Satisfaction interiorSatisfaction;

    @Enumerated(EnumType.STRING)
    private Satisfaction problemConfigurationSatisfaction;

    /**
     * common
     */
    private long likeCount;

    @Builder
    public Review(Long id, Member member, Theme theme, ReviewType reviewType, int recodeNumber, boolean clearYN, LocalTime clearTime, int hintUsageCount, int rating, String comment, Difficulty perceivedDifficulty, HorrorGrade perceivedHorrorGrade, Activity perceivedActivity, Satisfaction scenarioSatisfaction, Satisfaction interiorSatisfaction, Satisfaction problemConfigurationSatisfaction, long likeCount) {
        this.id = id;
        this.member = member;
        this.theme = theme;
        this.reviewType = reviewType;
        this.recodeNumber = recodeNumber;
        this.clearYN = clearYN;
        this.clearTime = clearTime;
        this.hintUsageCount = hintUsageCount;
        this.rating = rating;
        this.comment = comment;
        this.perceivedDifficulty = perceivedDifficulty;
        this.perceivedHorrorGrade = perceivedHorrorGrade;
        this.perceivedActivity = perceivedActivity;
        this.scenarioSatisfaction = scenarioSatisfaction;
        this.interiorSatisfaction = interiorSatisfaction;
        this.problemConfigurationSatisfaction = problemConfigurationSatisfaction;
        this.likeCount = likeCount;
    }

    public static Review create(Member member, Theme theme, int recodeNumber, ReviewCreateDto reviewCreateDto) {
        Review review = Review.builder()
                .member(member)
                .theme(theme)
                .reviewType(reviewCreateDto.getReviewType())
                .recodeNumber(recodeNumber)
                .clearYN(reviewCreateDto.isClearYN())
                .clearTime(reviewCreateDto.getClearTime())
                .hintUsageCount(reviewCreateDto.getHintUsageCount())
                .rating(reviewCreateDto.getRating())
                .comment(reviewCreateDto.getComment())
                .perceivedDifficulty(reviewCreateDto.getPerceivedDifficulty())
                .perceivedHorrorGrade(reviewCreateDto.getPerceivedHorrorGrade())
                .perceivedActivity(reviewCreateDto.getPerceivedActivity())
                .scenarioSatisfaction(reviewCreateDto.getScenarioSatisfaction())
                .interiorSatisfaction(reviewCreateDto.getInteriorSatisfaction())
                .problemConfigurationSatisfaction(reviewCreateDto.getProblemConfigurationSatisfaction())
                .likeCount(0)
                .build();

        if (reviewCreateDto.reviewImagesExists()) {
            List<ReviewImageDto> reviewImages = reviewCreateDto.getReviewImages();
            reviewImages.forEach(reviewImageDto -> review.addReviewImage(ReviewImage.create(reviewImageDto)));
        }

        return review;
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

    public void addReviewImage(ReviewImage reviewImage) {
        this.reviewImages.add(reviewImage);
        reviewImage.setReview(this);
    }

    public void addPerceivedThemeGenre(Genre genre) {
        ReviewPerceivedThemeGenre perceivedThemeGenre = ReviewPerceivedThemeGenre.builder()
                .review(this)
                .genre(genre)
                .build();

        this.perceivedThemeGenres.add(perceivedThemeGenre);
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

    public List<ReviewImage> getReviewImages() {
        return reviewImages;
    }

    public String getComment() {
        return comment;
    }

    public Difficulty getPerceivedDifficulty() {
        return perceivedDifficulty;
    }

    public HorrorGrade getPerceivedHorrorGrade() {
        return perceivedHorrorGrade;
    }

    public Activity getPerceivedActivity() {
        return perceivedActivity;
    }

    public Satisfaction getScenarioSatisfaction() {
        return scenarioSatisfaction;
    }

    public Satisfaction getInteriorSatisfaction() {
        return interiorSatisfaction;
    }

    public Satisfaction getProblemConfigurationSatisfaction() {
        return problemConfigurationSatisfaction;
    }

    public List<ReviewPerceivedThemeGenre> getPerceivedThemeGenreEntities() {
        return perceivedThemeGenres;
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

    public List<Genre> getPerceivedThemeGenres() {
        return this.perceivedThemeGenres.stream().map(ReviewPerceivedThemeGenre::getGenre).collect(Collectors.toList());
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount--;
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
                ", reviewImages=" + reviewImages +
                ", comment='" + comment + '\'' +
//                ", perceivedThemeGenres=" + perceivedThemeGenres +
                ", perceivedDifficulty=" + perceivedDifficulty +
                ", perceivedHorrorGrade=" + perceivedHorrorGrade +
                ", perceivedActivity=" + perceivedActivity +
                ", scenarioSatisfaction=" + scenarioSatisfaction +
                ", interiorSatisfaction=" + interiorSatisfaction +
                ", problemConfigurationSatisfaction=" + problemConfigurationSatisfaction +
                ", likeCount=" + likeCount +
                ", registerTimes=" + registerTimes +
                ", updateTimes=" + updateTimes +
                '}';
    }
}
