package bbangduck.bd.bbangduck.domain.review.entity;


import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.Satisfaction;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewSurveyCreateDto;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewSurveyUpdateDto;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @OneToMany(mappedBy = "reviewSurvey", cascade = CascadeType.ALL)
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

    @Builder
    public ReviewSurvey(Long id, Review review, Difficulty perceivedDifficulty, HorrorGrade perceivedHorrorGrade, Activity perceivedActivity, Satisfaction scenarioSatisfaction, Satisfaction interiorSatisfaction, Satisfaction problemConfigurationSatisfaction) {
        this.id = id;
        this.review = review;
        this.perceivedDifficulty = perceivedDifficulty;
        this.perceivedHorrorGrade = perceivedHorrorGrade;
        this.perceivedActivity = perceivedActivity;
        this.scenarioSatisfaction = scenarioSatisfaction;
        this.interiorSatisfaction = interiorSatisfaction;
        this.problemConfigurationSatisfaction = problemConfigurationSatisfaction;
    }

    public void addPerceivedThemeGenre(Genre genre) {
        ReviewPerceivedThemeGenre reviewPerceivedThemeGenre = ReviewPerceivedThemeGenre.builder()
                .reviewSurvey(this)
                .genre(genre)
                .build();

        this.perceivedThemeGenres.add(reviewPerceivedThemeGenre);
    }

    public static ReviewSurvey create(ReviewSurveyCreateDto reviewSurveyCreateDto) {
        return ReviewSurvey.builder()
                .review(null)
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

    public List<ReviewPerceivedThemeGenre> getPerceivedThemeGenresEntity() {
        return perceivedThemeGenres;
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
        return this.perceivedThemeGenres.stream().map(ReviewPerceivedThemeGenre::getGenre).collect(Collectors.toList());
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
