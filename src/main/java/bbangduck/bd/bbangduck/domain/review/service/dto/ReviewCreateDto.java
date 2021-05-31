package bbangduck.bd.bbangduck.domain.review.service.dto;

import bbangduck.bd.bbangduck.domain.model.emumerate.*;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 생성에 대한 Service 로직 구현 시 필요한 데이터들을 Dto 단위로 이동시키기 위해 구현한 Service Dto
 * Controller 단과의 의존 관계를 최소화 하기 위해 구현
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewCreateDto {

    /**
     * 간단 리뷰
     */

    private ReviewType reviewType;

    private boolean clearYN;

    private LocalTime clearTime;

    private Integer hintUsageCount;

    private Integer rating;

    private List<Long> friendIds;

    /**
     * 상세 리뷰
     */

    private List<ReviewImageDto> reviewImages;

    private String comment;

    /**
     * 테마 설문 조사 추가 작성 리뷰
     */
    private List<String> genreCodes;

    private Difficulty perceivedDifficulty;

    private HorrorGrade perceivedHorrorGrade;

    private Activity perceivedActivity;

    private Satisfaction scenarioSatisfaction;

    private Satisfaction interiorSatisfaction;

    private Satisfaction problemConfigurationSatisfaction;

    @Builder
    public ReviewCreateDto(ReviewType reviewType, boolean clearYN, LocalTime clearTime, Integer hintUsageCount, Integer rating, List<Long> friendIds, List<ReviewImageDto> reviewImages, String comment, List<String> genreCodes, Difficulty perceivedDifficulty, HorrorGrade perceivedHorrorGrade, Activity perceivedActivity, Satisfaction scenarioSatisfaction, Satisfaction interiorSatisfaction, Satisfaction problemConfigurationSatisfaction) {
        this.reviewType = reviewType;
        this.clearYN = clearYN;
        this.clearTime = clearTime;
        this.hintUsageCount = hintUsageCount;
        this.rating = rating;
        this.friendIds = friendIds;
        this.reviewImages = reviewImages;
        this.comment = comment;
        this.genreCodes = genreCodes;
        this.perceivedDifficulty = perceivedDifficulty;
        this.perceivedHorrorGrade = perceivedHorrorGrade;
        this.perceivedActivity = perceivedActivity;
        this.scenarioSatisfaction = scenarioSatisfaction;
        this.interiorSatisfaction = interiorSatisfaction;
        this.problemConfigurationSatisfaction = problemConfigurationSatisfaction;
    }

    public ReviewType getReviewType() {
        return reviewType;
    }

    public LocalTime getClearTime() {
        return clearTime;
    }

    public Integer getHintUsageCount() {
        return hintUsageCount;
    }

    public Integer getRating() {
        return rating;
    }

    public List<ReviewImageDto> getReviewImages() {
        return reviewImages;
    }

    public String getComment() {
        return comment;
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

    public List<String> getGenreCodes() {
        return genreCodes;
    }

    public List<Long> getFriendIds() {
        return friendIds;
    }

    public boolean reviewImagesExists() {
        return reviewImages != null && !reviewImages.isEmpty();
    }

    public boolean isClearYN() {
        return clearYN;
    }
}
