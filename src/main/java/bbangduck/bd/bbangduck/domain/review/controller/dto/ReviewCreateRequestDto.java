package bbangduck.bd.bbangduck.domain.review.controller.dto;

import bbangduck.bd.bbangduck.domain.model.emumerate.*;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewImageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

// TODO: 2021-05-23 주석 달기
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateRequestDto {

    /**
     * 간단 리뷰 요청 body
     */

    @NotNull(message = "리뷰 타입을 기입해 주세요. [SIMPLE, DETAIL, DEEP]")
    private ReviewType reviewType;

    @NotNull(message = "게임 클리어 시간을 기입해 주세요.")
    private LocalTime clearTime;

    @NotNull(message = "사용한 힌트 개수를 기입해 주세요.")
    private Integer hintUsageCount;

    @NotNull(message = "테마에 대한 평점을 기입해 주세요.")
    private Integer rating;

    /**
     * 상세 리뷰 요청 body
     */

    private List<ReviewImageRequestDto> reviewImages = null;

    @Length(max = 3000)
    private String comment;

    /**
     * 테마 설문 조사 추가 작성 리뷰 요청 body
     */
    // TODO: 2021-05-25 리뷰 체감 장르 부분 추가
    private List<String> genreCodes;

    private Difficulty perceivedDifficulty;

    private HorrorGrade perceivedHorrorGrade;

    private Activity perceivedActivity;

    private Satisfaction scenarioSatisfaction;

    private Satisfaction interiorSatisfaction;

    private Satisfaction problemConfigurationSatisfaction;

    public boolean isSimpleReview() {
        return !reviewImagesExists() && commentNotExists() && perceivedDifficultyIsNull() && perceivedHorrorGradeIsNull() &&
                perceivedActivityIsNull() && scenarioSatisfactionIsNull() && interiorSatisfactionIsNull() &&
                problemConfigurationSatisfactionIsNull();
    }

    public boolean perceivedDifficultyIsNull() {
        return perceivedDifficulty == null;
    }

    public boolean perceivedHorrorGradeIsNull() {
        return perceivedHorrorGrade == null;
    }

    public boolean perceivedActivityIsNull() {
        return perceivedActivity == null;
    }

    public boolean scenarioSatisfactionIsNull() {
        return scenarioSatisfaction == null;
    }

    public boolean interiorSatisfactionIsNull() {
        return interiorSatisfaction == null;
    }

    public boolean problemConfigurationSatisfactionIsNull() {
        return problemConfigurationSatisfaction == null;
    }

    public boolean commentNotExists() {
        return comment == null || comment.isBlank();
    }

    public boolean reviewImagesExists() {
        return reviewImages != null && !reviewImages.isEmpty();
    }

    public boolean isDetailReview() {
        return perceivedDifficultyIsNull() && perceivedHorrorGradeIsNull() &&
                perceivedActivityIsNull() && scenarioSatisfactionIsNull() && interiorSatisfactionIsNull() &&
                problemConfigurationSatisfactionIsNull();
    }

    public ReviewCreateDto toServiceDto() {
        List<ReviewImageDto> reviewImageDtos = reviewImages.stream().map(ReviewImageRequestDto::toServiceDto).collect(Collectors.toList());

        return ReviewCreateDto.builder()
                .reviewType(reviewType)
                .clearTime(clearTime)
                .hintUsageCount(hintUsageCount)
                .rating(rating)
                .reviewImages(reviewImageDtos)
                .comment(comment)
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
