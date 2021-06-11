package bbangduck.bd.bbangduck.domain.review.controller.dto;

import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.Satisfaction;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewSurveyUpdateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 작성자 : 정구민 <br>
 * 작성 일자 : 2021-06-11 <br><br>
 *
 * 리뷰에 등록된 설문 수정 시 요청 Body 의 Data 를 담을 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewSurveyUpdateRequestDto {

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

    public ReviewSurveyUpdateDto toServiceDto() {
        return ReviewSurveyUpdateDto.builder()
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
