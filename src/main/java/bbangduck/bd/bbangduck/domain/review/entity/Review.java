package bbangduck.bd.bbangduck.domain.review.entity;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 작성자 : 정구민 <br><br>
 *
 * Review Entity
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

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

    private ReviewType reviewType;

    private LocalTime clearTime;

    private int hintUsageCount;

    private int rating;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewImage> reviewImages;

    @Column(length = 3000)
    private String comment;

    private String perceivedDifficulty;

    private String perceivedHorrorGrade;

    private String perceivedActivity;

    private String scenarioSatisfaction;

    private String interiorSatisfaction;

    private String problemConfigurationSatisfaction;

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

    public String getPerceivedDifficulty() {
        return perceivedDifficulty;
    }

    public String getPerceivedHorrorGrade() {
        return perceivedHorrorGrade;
    }

    public String getPerceivedActivity() {
        return perceivedActivity;
    }

    public String getScenarioSatisfaction() {
        return scenarioSatisfaction;
    }

    public String getInteriorSatisfaction() {
        return interiorSatisfaction;
    }

    public String getProblemConfigurationSatisfaction() {
        return problemConfigurationSatisfaction;
    }
}
