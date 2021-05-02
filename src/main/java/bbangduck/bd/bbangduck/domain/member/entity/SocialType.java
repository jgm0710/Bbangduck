package bbangduck.bd.bbangduck.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원에 등록된 Social 인증 정보가 어떤 소셜 인증을 통해 생성되었는지를 명시하기 위한 Enum
 */
// FIXME: 2021-05-02 Getter, Builder 를 롬복을 사용하지 않고 구현
@Getter
@RequiredArgsConstructor
public enum SocialType {
    KAKAO("카카오"),
    NAVER("네이버");

    private final String description;
}
