package bbangduck.bd.bbangduck.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialType {
    KAKAO("카카오"),
    NAVER("네이버");

    private final String description;
}
