package bbangduck.bd.bbangduck.domain.review.controller.dto;

import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.Satisfaction;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewPerceivedThemeGenre;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 조회 시 해당 리뷰가 상세 및 추가 설문 작성 리뷰일 경우 해당 Dto 를 통해서 Data 응답
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeepReviewResponseDto extends DetailReviewResponseDto {

    private List<ReviewPerceivedThemeGenreResponseDto> reviewPerceivedThemeGenres;

    private Difficulty perceivedDifficulty;

    private HorrorGrade perceivedHorrorGrade;

    private Activity perceivedActivity;

    private Satisfaction scenarioSatisfaction;

    private Satisfaction interiorSatisfaction;

    private Satisfaction problemConfigurationSatisfaction;

    public static DeepReviewResponseDto convert(Review review, Member currentMember, boolean existReviewLike) {
        return new DeepReviewResponseDto(review, currentMember, existReviewLike);
    }

    protected DeepReviewResponseDto(Review review, Member currentMember, boolean existReviewLike) {
        super(review, currentMember, existReviewLike);

        this.reviewPerceivedThemeGenres = convertReviewPerceivedThemeGenres(review.getPerceivedThemeGenres());
        this.perceivedDifficulty = review.getPerceivedDifficulty();
        this.perceivedHorrorGrade = review.getPerceivedHorrorGrade();
        this.perceivedActivity = review.getPerceivedActivity();
        this.scenarioSatisfaction = review.getScenarioSatisfaction();
        this.interiorSatisfaction = review.getInteriorSatisfaction();
        this.problemConfigurationSatisfaction = review.getProblemConfigurationSatisfaction();
    }

    private List<ReviewPerceivedThemeGenreResponseDto> convertReviewPerceivedThemeGenres(List<Genre> perceivedThemeGenres) {
        if (perceivedThemeGenresExists(perceivedThemeGenres)) {
            return perceivedThemeGenres.stream()
                    .map(ReviewPerceivedThemeGenreResponseDto::convert)
                    .collect(Collectors.toList());
        }
        return null;
    }

    private boolean perceivedThemeGenresExists(List<Genre> perceivedThemeGenres) {
        return perceivedThemeGenres != null && !perceivedThemeGenres.isEmpty();
    }
}
