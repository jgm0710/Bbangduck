package bbangduck.bd.bbangduck.domain.review.entity;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.model.emumerate.*;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.List;

/**
 * 작성자 : 정구민 <br><br>
 *
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

    private int hintUsageCount;

    private int rating;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewImage> reviewImages;

    @Column(length = 3000)
    private String comment;

    @Enumerated(EnumType.STRING)
    private Difficulty perceivedDifficulty;

    @Enumerated(EnumType.STRING)
    private HorrorGrade perceivedHorrorGrade;

    @Enumerated(EnumType.STRING)
    private Activity perceivedActivity;

    @Enumerated(EnumType.STRING)
    private ScenarioSatisfaction scenarioSatisfaction;

    @Enumerated(EnumType.STRING)
    private InteriorSatisfaction interiorSatisfaction;

    @Enumerated(EnumType.STRING)
    private ProblemConfigurationSatisfaction problemConfigurationSatisfaction;

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

    public ScenarioSatisfaction getScenarioSatisfaction() {
        return scenarioSatisfaction;
    }

    public InteriorSatisfaction getInteriorSatisfaction() {
        return interiorSatisfaction;
    }

    public ProblemConfigurationSatisfaction getProblemConfigurationSatisfaction() {
        return problemConfigurationSatisfaction;
    }

}
