package bbangduck.bd.bbangduck.member.social.exception;

import bbangduck.bd.bbangduck.member.social.SocialType;

public class SocialUserInfoRetrievalErrorException extends RuntimeException {

    public SocialUserInfoRetrievalErrorException(SocialType socialType) {
        super("소셜 회원 정보 조회 중 에러 발생, SocialType : "+socialType.name());
    }
}
