package bbangduck.bd.bbangduck.domain.member.enumerate;

import bbangduck.bd.bbangduck.global.common.EnumType;
import lombok.RequiredArgsConstructor;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원에 등록된 Social 인증 정보가 어떤 소셜 인증을 통해 생성되었는지를 명시하기 위한 Enum
 */
@RequiredArgsConstructor
public enum SocialType implements EnumType {
    KAKAO("카카오"),
    NAVER("네이버");

    private final String description;

    @Override
    public String getDescription() {
        return description;
    }
}
