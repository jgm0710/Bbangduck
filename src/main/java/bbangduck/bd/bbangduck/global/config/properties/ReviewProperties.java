package bbangduck.bd.bbangduck.global.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰와 관련된 설정 값들. 예를 들면 리뷰 설문 등록 가능 기간, 리뷰 생성 시 함께 플레이 한 친구 수 제한 개수 등에
 * 대한 값들을 설정 파일로 관리하기 위해 구현한 Properties
 */
@Component
@Getter
@Setter
@ConfigurationProperties("review")
public class ReviewProperties {

    private long periodForAddingSurveys;

    private int playTogetherFriendsCountLimit;

    private int perceivedThemeGenresCountLimit;

}
