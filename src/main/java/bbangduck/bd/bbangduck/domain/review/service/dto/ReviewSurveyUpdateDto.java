package bbangduck.bd.bbangduck.domain.review.service.dto;

import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.Satisfaction;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 작성자 : 정구민 <br>
 * 작성 일자 : 2021-06-11 <br><br>
 *
 * 리뷰에 등록된 설문 수정 시 필요한 Data 를 담을 Service Dto
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewSurveyUpdateDto {

    private List<String> genreCodes;

    private Difficulty perceivedDifficulty;

    private HorrorGrade perceivedHorrorGrade;

    private Activity perceivedActivity;

    private Satisfaction scenarioSatisfaction;

    private Satisfaction interiorSatisfaction;

    private Satisfaction problemConfigurationSatisfaction;

    @Builder
    public ReviewSurveyUpdateDto(List<String> genreCodes, Difficulty perceivedDifficulty, HorrorGrade perceivedHorrorGrade, Activity perceivedActivity, Satisfaction scenarioSatisfaction, Satisfaction interiorSatisfaction, Satisfaction problemConfigurationSatisfaction) {
        this.genreCodes = genreCodes;
        this.perceivedDifficulty = perceivedDifficulty;
        this.perceivedHorrorGrade = perceivedHorrorGrade;
        this.perceivedActivity = perceivedActivity;
        this.scenarioSatisfaction = scenarioSatisfaction;
        this.interiorSatisfaction = interiorSatisfaction;
        this.problemConfigurationSatisfaction = problemConfigurationSatisfaction;
    }

    public List<String> getGenreCodes() {
        return genreCodes;
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
}
