package bbangduck.bd.bbangduck.domain.review.entity;


import bbangduck.bd.bbangduck.domain.genre.Genre;
import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.Satisfaction;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewSurveyCreateDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewSurveyUpdateDto;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰에 대한 추가 설문 내용을 저장할 Entity
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewSurvey extends BaseEntityDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_survey_id")
    private Long id;

    @OneToOne(mappedBy = "reviewSurvey")
    private Review review;

//    @OneToMany(mappedBy = "reviewSurvey", cascade = CascadeType.ALL)
//    private List<ReviewPerceivedThemeGenre> perceivedThemeGenres = new ArrayList<>();

    @ElementCollection(targetClass = Genre.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "review_perceived_theme_genres", joinColumns = @JoinColumn(name = "review_survey_id"))
    @Enumerated(EnumType.STRING)
    private List<Genre> perceivedThemeGenres;

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

    @Builder
    public ReviewSurvey(Long id, Review review, List<Genre> perceivedThemeGenres, Difficulty perceivedDifficulty, HorrorGrade perceivedHorrorGrade, Activity perceivedActivity, Satisfaction scenarioSatisfaction, Satisfaction interiorSatisfaction, Satisfaction problemConfigurationSatisfaction) {
        this.id = id;
        this.review = review;
        this.perceivedThemeGenres = perceivedThemeGenres;
        this.perceivedDifficulty = perceivedDifficulty;
        this.perceivedHorrorGrade = perceivedHorrorGrade;
        this.perceivedActivity = perceivedActivity;
        this.scenarioSatisfaction = scenarioSatisfaction;
        this.interiorSatisfaction = interiorSatisfaction;
        this.problemConfigurationSatisfaction = problemConfigurationSatisfaction;
    }

    public static ReviewSurvey create(ReviewSurveyCreateDto reviewSurveyCreateDto) {
        return ReviewSurvey.builder()
                .review(null)
                .perceivedThemeGenres(reviewSurveyCreateDto.getPerceivedThemeGenres())
                .perceivedDifficulty(reviewSurveyCreateDto.getPerceivedDifficulty())
                .perceivedHorrorGrade(reviewSurveyCreateDto.getPerceivedHorrorGrade())
                .perceivedActivity(reviewSurveyCreateDto.getPerceivedActivity())
                .scenarioSatisfaction(reviewSurveyCreateDto.getScenarioSatisfaction())
                .interiorSatisfaction(reviewSurveyCreateDto.getInteriorSatisfaction())
                .problemConfigurationSatisfaction(reviewSurveyCreateDto.getProblemConfigurationSatisfaction())
                .build();
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public Long getId() {
        return id;
    }

    public Review getReview() {
        return review;
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

    public List<Genre> getPerceivedThemeGenres() {
        return perceivedThemeGenres;
    }

    public void update(ReviewSurveyUpdateDto reviewSurveyUpdateDto) {
        this.perceivedDifficulty = reviewSurveyUpdateDto.getPerceivedDifficulty();
        this.perceivedHorrorGrade = reviewSurveyUpdateDto.getPerceivedHorrorGrade();
        this.perceivedActivity = reviewSurveyUpdateDto.getPerceivedActivity();
        this.scenarioSatisfaction = reviewSurveyUpdateDto.getScenarioSatisfaction();
        this.interiorSatisfaction = reviewSurveyUpdateDto.getInteriorSatisfaction();
        this.problemConfigurationSatisfaction = reviewSurveyUpdateDto.getProblemConfigurationSatisfaction();
    }
}
