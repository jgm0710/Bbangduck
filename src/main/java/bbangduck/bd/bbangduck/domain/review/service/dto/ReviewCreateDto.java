package bbangduck.bd.bbangduck.domain.review.service.dto;

import bbangduck.bd.bbangduck.domain.model.emumerate.*;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

// TODO: 2021-05-23 주석 달기
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewCreateDto {

    /**
     * 간단 리뷰
     */

    private ReviewType reviewType;

    private LocalTime clearTime;

    private Integer hintUsageCount;

    private Integer rating;

    /**
     * 상세 리뷰
     */

    private List<ReviewImageDto> reviewImages;

    private String comment;

    /**
     * 테마 설문 조사 추가 작성 리뷰
     */

    private Difficulty perceivedDifficulty;

    private HorrorGrade perceivedHorrorGrade;

    private Activity perceivedActivity;

    private ScenarioSatisfaction scenarioSatisfaction;

    private InteriorSatisfaction interiorSatisfaction;

    private ProblemConfigurationSatisfaction problemConfigurationSatisfaction;

    @Builder
    public ReviewCreateDto(ReviewType reviewType, LocalTime clearTime, Integer hintUsageCount, Integer rating, List<ReviewImageDto> reviewImages, String comment, Difficulty perceivedDifficulty, HorrorGrade perceivedHorrorGrade, Activity perceivedActivity, ScenarioSatisfaction scenarioSatisfaction, InteriorSatisfaction interiorSatisfaction, ProblemConfigurationSatisfaction problemConfigurationSatisfaction) {
        this.reviewType = reviewType;
        this.clearTime = clearTime;
        this.hintUsageCount = hintUsageCount;
        this.rating = rating;
        this.reviewImages = reviewImages;
        this.comment = comment;
        this.perceivedDifficulty = perceivedDifficulty;
        this.perceivedHorrorGrade = perceivedHorrorGrade;
        this.perceivedActivity = perceivedActivity;
        this.scenarioSatisfaction = scenarioSatisfaction;
        this.interiorSatisfaction = interiorSatisfaction;
        this.problemConfigurationSatisfaction = problemConfigurationSatisfaction;
    }

    public ReviewType getReviewType() {
        return reviewType;
    }

    public LocalTime getClearTime() {
        return clearTime;
    }

    public Integer getHintUsageCount() {
        return hintUsageCount;
    }

    public Integer getRating() {
        return rating;
    }

    public List<ReviewImageDto> getReviewImages() {
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
