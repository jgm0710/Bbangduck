package bbangduck.bd.bbangduck.domain.review.controller.dto;

import bbangduck.bd.bbangduck.domain.model.emumerate.*;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewImageDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 생성 시 클라이언트로부터 리뷰 생성에 필요한 데이터를 받을 때 사용할 Dto
 */
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

    @NotNull(message = "게임 클리어 여부를 기입해주세요.")
    private Boolean clearYN;

    @NotNull(message = "게임 클리어 시간을 기입해 주세요.")
    private LocalTime clearTime;

    @NotNull(message = "사용한 힌트 개수를 기입해 주세요.")
    private Integer hintUsageCount;

    @NotNull(message = "테마에 대한 평점을 기입해 주세요.")
    private Integer rating;

    private List<Long> friendIds;

    /**
     * 상세 리뷰 요청 body
     */

    private List<ReviewImageRequestDto> reviewImages;

    @Length(max = 3000)
    private String comment;

    /**
     * 테마 설문 조사 추가 작성 리뷰 요청 body
     */
    private List<String> genreCodes;

    private Difficulty perceivedDifficulty;

    private HorrorGrade perceivedHorrorGrade;

    private Activity perceivedActivity;

    private Satisfaction scenarioSatisfaction;

    private Satisfaction interiorSatisfaction;

    private Satisfaction problemConfigurationSatisfaction;

    @JsonIgnore
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

    public boolean genreCodesExists() {
        return genreCodes != null && !genreCodes.isEmpty();
    }

    @JsonIgnore
    public boolean isDetailReview() {
        return perceivedDifficultyIsNull() && perceivedHorrorGradeIsNull() &&
                perceivedActivityIsNull() && scenarioSatisfactionIsNull() && interiorSatisfactionIsNull() &&
                problemConfigurationSatisfactionIsNull();
    }

    public boolean friendIdsExists() {
        return friendIds != null && !friendIds.isEmpty();
    }

    public ReviewCreateDto toServiceDto() {

        List<ReviewImageDto> reviewImageDtos = null;
        if (reviewImagesExists()) {
            reviewImageDtos = reviewImages.stream().map(ReviewImageRequestDto::toServiceDto).collect(Collectors.toList());
        }

        return ReviewCreateDto.builder()
                .reviewType(reviewType)
                .clearYN(clearYN)
                .clearTime(clearTime)
                .hintUsageCount(hintUsageCount)
                .rating(rating)
                .friendIds(friendIds)
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
