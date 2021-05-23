package bbangduck.bd.bbangduck.domain.review.controller.dto;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewImage;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewType;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewImageDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
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

    private String perceivedDifficulty;

    private String perceivedHorrorGrade;

    private String perceivedActivity;

    private String scenarioSatisfaction;

    private String interiorSatisfaction;

    private String problemConfigurationSatisfaction;

    public boolean isSimpleReview() {
        return !reviewImagesExists() && !commentExists() && !perceivedDifficultyExists() && !perceivedHorrorGradeExists() &&
                !perceivedActivityExists() && !scenarioSatisfactionExists() && !interiorSatisfactionExists() &&
                !problemConfigurationSatisfactionExists();
    }

    public boolean perceivedDifficultyExists() {
        return perceivedDifficulty != null && !perceivedDifficulty.isBlank();
    }

    public boolean perceivedHorrorGradeExists() {
        return perceivedHorrorGrade != null && !perceivedHorrorGrade.isBlank();
    }

    public boolean perceivedActivityExists() {
        return perceivedActivity != null && !perceivedActivity.isBlank();
    }

    public boolean scenarioSatisfactionExists() {
        return scenarioSatisfaction != null && !scenarioSatisfaction.isBlank();
    }

    public boolean interiorSatisfactionExists() {
        return interiorSatisfaction != null && !interiorSatisfaction.isBlank();
    }

    public boolean problemConfigurationSatisfactionExists() {
        return problemConfigurationSatisfaction != null && !problemConfigurationSatisfaction.isBlank();
    }

    public boolean commentExists() {
        return comment != null;
    }

    public boolean reviewImagesExists() {
        return reviewImages != null && !reviewImages.isEmpty();
    }

    public boolean isDetailReview() {
        return !perceivedDifficultyExists() && !perceivedHorrorGradeExists() &&
                !perceivedActivityExists() && !scenarioSatisfactionExists() && !interiorSatisfactionExists() &&
                !problemConfigurationSatisfactionExists();
    }

    public ReviewCreateDto toServiceDto() {
        List<ReviewImageDto> reviewImageDtos = reviewImages.stream().map(reviewImageRequestDto -> reviewImageRequestDto.toServiceDto()).collect(Collectors.toList());

        return ReviewCreateDto.builder()
                .reviewType(reviewType)
                .clearTime(clearTime)
                .hintUsageCount(hintUsageCount)
                .rating(rating)
                .reviewImages(reviewImageDtos)
                .comment(comment)
                .perceivedDifficulty(perceivedDifficulty)
                .perceivedHorrorGrade(perceivedHorrorGrade)
                .perceivedActivity(perceivedActivity)
                .scenarioSatisfaction(scenarioSatisfaction)
                .interiorSatisfaction(interiorSatisfaction)
                .problemConfigurationSatisfaction(problemConfigurationSatisfaction)
                .build();
    }
}
