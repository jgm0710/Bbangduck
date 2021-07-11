package bbangduck.bd.bbangduck.domain.review.dto.controller.response;

import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.Satisfaction;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewSurvey;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 작성자 : 정구민 <br>
 * 작성 일자 : 2021-06-11 <br><br>
 *
 * 리뷰 조회 시 간단 리뷰에 설문이 등록되어 있는 경우 응답될 응답 Body Data 를 담을 Dto
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SimpleAndSurveyReviewResponseDto extends SimpleReviewResponseDto {

    private List<ReviewPerceivedThemeGenreResponseDto> perceivedThemeGenres;
    private Difficulty perceivedDifficulty;
    private HorrorGrade perceivedHorrorGrade;
    private Activity perceivedActivity;
    private Satisfaction scenarioSatisfaction;
    private Satisfaction interiorSatisfaction;
    private Satisfaction problemConfigurationSatisfaction;

    public static SimpleAndSurveyReviewResponseDto convert(Review review, Member currentMember, boolean existReviewLike, boolean possibleOfAddReviewSurvey) {
        return new SimpleAndSurveyReviewResponseDto(review, currentMember, existReviewLike, possibleOfAddReviewSurvey);
    }

    protected SimpleAndSurveyReviewResponseDto(Review review, Member currentMember, boolean existReviewLike, boolean possibleOfAddReviewSurvey) {
        super(review, currentMember, existReviewLike, possibleOfAddReviewSurvey);

        ReviewSurvey reviewSurvey = review.getReviewSurvey();
        List<Genre> perceivedThemeGenres = reviewSurvey.getPerceivedThemeGenres();

        this.perceivedThemeGenres = perceivedThemeGenres.stream().map(ReviewPerceivedThemeGenreResponseDto::convert).collect(Collectors.toList());
        this.perceivedDifficulty = reviewSurvey.getPerceivedDifficulty();
        this.perceivedHorrorGrade = reviewSurvey.getPerceivedHorrorGrade();
        this.perceivedActivity = reviewSurvey.getPerceivedActivity();
        this.scenarioSatisfaction = reviewSurvey.getScenarioSatisfaction();
        this.interiorSatisfaction = reviewSurvey.getInteriorSatisfaction();
        this.problemConfigurationSatisfaction = reviewSurvey.getProblemConfigurationSatisfaction();

        super.surveyYN = true;
    }
}
