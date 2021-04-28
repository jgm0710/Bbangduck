package bbangduck.bd.bbangduck.security.kakao.exception;

import bbangduck.bd.bbangduck.member.social.SocialType;

public class SocialAccessTokenRetrievalErrorException extends RuntimeException {
    public SocialAccessTokenRetrievalErrorException(SocialType socialType) {
        super("소셜 액세스 토큰을 검색하는 중에 오류가 발생했습니다. SocialType : " + socialType.name());
    }
}
