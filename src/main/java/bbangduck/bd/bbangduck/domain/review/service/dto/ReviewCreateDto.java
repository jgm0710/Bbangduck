package bbangduck.bd.bbangduck.domain.review.service.dto;

import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewImageRequestDto;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
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

    private String perceivedDifficulty;

    private String perceivedHorrorGrade;

    private String perceivedActivity;

    private String scenarioSatisfaction;

    private String interiorSatisfaction;

    private String problemConfigurationSatisfaction;

    @Builder
    public ReviewCreateDto(ReviewType reviewType, LocalTime clearTime, Integer hintUsageCount, Integer rating, List<ReviewImageDto> reviewImages, String comment, String perceivedDifficulty, String perceivedHorrorGrade, String perceivedActivity, String scenarioSatisfaction, String interiorSatisfaction, String problemConfigurationSatisfaction) {
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
