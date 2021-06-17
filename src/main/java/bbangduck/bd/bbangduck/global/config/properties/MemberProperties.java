package bbangduck.bd.bbangduck.global.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 작성자 : Gumin Jeong
 * <p>
 * 작성 일자 : 2021-06-17
 * <p>
 * 회원 비즈니스 로직 구현 시 회원 플레이 성향 조회 개수 등의 설정 파일로 관리하면 편한 값들을
 * 정의하기 위해 구현한 Properties
 */
@Component
@ConfigurationProperties("member")
@Getter
@Setter
public class MemberProperties {

    private int playInclinationTopLimit;
}
