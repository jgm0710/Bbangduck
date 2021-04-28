package bbangduck.bd.bbangduck.security.exception;

import bbangduck.bd.bbangduck.common.ExceptionStatus;
import bbangduck.bd.bbangduck.member.social.SocialUserInfoDto;
import lombok.Getter;

@Getter
public class SocialUserNotFoundException extends RuntimeException {

    private int status;

    private SocialUserInfoDto socialUserInfoDto;

    private String viewName;

    public SocialUserNotFoundException(ExceptionStatus exceptionStatus, SocialUserInfoDto socialUserInfoDto) {
        super(exceptionStatus.getMessage());
        this.status = exceptionStatus.getStatus();
        this.socialUserInfoDto = socialUserInfoDto;
        this.viewName = "social-sign-in-fail";
    }
}
