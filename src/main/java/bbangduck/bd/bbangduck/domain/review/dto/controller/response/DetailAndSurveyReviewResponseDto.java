package bbangduck.bd.bbangduck.domain.review.dto.controller.response;

import bbangduck.bd.bbangduck.domain.genre.Genre;
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

/**
 * 작성자 : 정구민 <br>
 * 작성 일자 : 2021-06-11 <br><br>
 *
 * 리뷰 조회 시 상세 리뷰에 설문이 등록되어 있는 경우 응답될 응답 Body Data 를 담을 Dto
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DetailAndSurveyReviewResponseDto extends DetailReviewResponseDto {

    private List<Genre> perceivedThemeGenres;
    private Difficulty perceivedDifficulty;
    private HorrorGrade perceivedHorrorGrade;
    private Activity perceivedActivity;
    private Satisfaction scenarioSatisfaction;
    private Satisfaction interiorSatisfaction;
    private Satisfaction problemConfigurationSatisfaction;

    public static DetailAndSurveyReviewResponseDto convert(Review review, Member currentMember, boolean existReviewLike, boolean possibleOfAddReviewSurvey) {
        return new DetailAndSurveyReviewResponseDto(review, currentMember, existReviewLike, possibleOfAddReviewSurvey);
    }

    protected DetailAndSurveyReviewResponseDto(Review review, Member currentMember, boolean existReviewLike, boolean possibleOfAddReviewSurvey) {
        super(review, currentMember, existReviewLike, possibleOfAddReviewSurvey);

        ReviewSurvey reviewSurvey = review.getReviewSurvey();

        this.perceivedThemeGenres = reviewSurvey.getPerceivedThemeGenres();
        this.perceivedDifficulty = reviewSurvey.getPerceivedDifficulty();
        this.perceivedHorrorGrade = reviewSurvey.getPerceivedHorrorGrade();
        this.perceivedActivity = reviewSurvey.getPerceivedActivity();
        this.scenarioSatisfaction = reviewSurvey.getScenarioSatisfaction();
        this.interiorSatisfaction = reviewSurvey.getInteriorSatisfaction();
        this.problemConfigurationSatisfaction = reviewSurvey.getProblemConfigurationSatisfaction();

        super.surveyYN = true;
    }
}

