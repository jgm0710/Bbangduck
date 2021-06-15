package bbangduck.bd.bbangduck.domain.review.dto.controller.request;

import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.Satisfaction;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewDetailCreateDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewSurveyCreateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 작성자 : Gumin Jeong
 * <p>
 * 작성 일자 : 2021-06-15
 * <p>
 * 리뷰에 리뷰 상세 및 설문 등록 요청 시 필요한 Data 를 담을 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDetailAndSurveyCreateDtoRequestDto {

    private List<ReviewImageRequestDto> reviewImages;

    @NotBlank(message = "상세 리뷰에 등록할 코멘트를 기입해 주세요.")
    @Length(max = 2000, message = "코멘트는 2000자를 넘길 수 없습니다.")
    private String comment;

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

    public ReviewDetailCreateDto toDetailServiceDto() {
        return ReviewDetailCreateDto.builder()
                .reviewImageDtos(this.reviewImages.stream().map(ReviewImageRequestDto::toServiceDto).collect(Collectors.toList()))
                .comment(this.comment)
                .build();
    }

    public ReviewSurveyCreateDto toSurveyServiceDto() {
        return ReviewSurveyCreateDto.builder()
                .genreCodes(this.genreCodes)
                .perceivedDifficulty(this.perceivedDifficulty)
                .perceivedHorrorGrade(this.perceivedHorrorGrade)
                .perceivedActivity(this.perceivedActivity)
                .scenarioSatisfaction(this.scenarioSatisfaction)
                .interiorSatisfaction(this.interiorSatisfaction)
                .problemConfigurationSatisfaction(this.problemConfigurationSatisfaction)
                .build();
    }

}
