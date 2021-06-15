package bbangduck.bd.bbangduck.domain.review.dto.controller.request;

import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.Satisfaction;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewSurveyCreateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰에 설문 추가 시 필요한 요청 Body 값들을 담을 Controller Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewSurveyCreateRequestDto {

    @NotEmpty(message = "테마 체감 장르에 대한 코드를 기입해 주세요.")
    private List<String> genreCodes;

    @NotNull(message = "채감 난이도를 기입해 주세요.")
    private Difficulty perceivedDifficulty;

    @NotNull(message = "채감 공포도를 기입해 주세요.")
    private HorrorGrade perceivedHorrorGrade;

    @NotNull(message = "채감 활동성을 기입해 주세요.")
    private Activity perceivedActivity;

    @NotNull(message = "시나리오 만족도를 기입해 주세요.")
    private Satisfaction scenarioSatisfaction;

    @NotNull(message = "인테리어 만족도를 기입해 주세요.")
    private Satisfaction interiorSatisfaction;

    @NotNull(message = "문제 구성 만족도를 기입해 주세요.")
    private Satisfaction problemConfigurationSatisfaction;

    public ReviewSurveyCreateDto toServiceDto() {
        return ReviewSurveyCreateDto.builder()
                .genreCodes(genreCodes)
                .perceivedDifficulty(perceivedDifficulty)
                .perceivedHorrorGrade(perceivedHorrorGrade)
                .perceivedActivity(perceivedActivity)
                .scenarioSatisfaction(scenarioSatisfaction)
                .interiorSatisfaction(interiorSatisfaction)
                .problemConfigurationSatisfaction(problemConfigurationSatisfaction)
                .build();
    }
}
